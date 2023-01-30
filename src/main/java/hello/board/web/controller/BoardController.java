package hello.board.web.controller;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.domain.image.Image;
import hello.board.domain.paging.PageMaker;
import hello.board.domain.repository.BoardRepository;
import hello.board.domain.repository.CommentRepository;
import hello.board.domain.repository.ImageRepository;
import hello.board.web.RedirectDTO;
import hello.board.web.auth.PrincipalDetails;
import hello.board.web.file.ImageStore;
import hello.board.web.form.BoardEditForm;
import hello.board.web.form.BoardSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
// final, @NonNull 필드만 받는 생성자
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardRepository boardRepository;

    private final ImageRepository imageRepository;

    private final CommentRepository commentRepository;

    /**
     * 전체 글 List<Board> 를 페이징 하여 모델에 담아 /list 뷰 호출
     * 쿼리파라미터로 currentPage 를 받아 Criteria 에 맵핑한다. (setter 사용, 생성자 (아마) X )
     * beanvalidation 이 아닌 jquery 로 검증했음
     */
    @GetMapping(value = {"/list", "/"})
    public String pagedList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                            Model model,
                            @ModelAttribute("criteria") Criteria criteria) {

        // 글 가져오기
        List<Board> pagedBoard = boardRepository.findPagedBoard(criteria);

        // 총 글갯수 가져오기
        Integer countTotalBoard = boardRepository.countTotalBoard(criteria);

        // 페이징 할 정보 설정하기
        PageMaker pageMaker = new PageMaker(criteria, countTotalBoard);

        // 페이지메이커, 글 목록 모델에 넣기
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("boardList", pagedBoard);

        // 로그인 중인 사용자 있음
        /*
        if (principalDetails != null) {
            model.addAttribute("member", principalDetails.getMember());
        }
        */
        
        return "/board/list";
    }

    /**
     * 요청파라미터로 boardId 를 받아 boardVO 객체를 찾아 Model 에 담고 /read 뷰 호출
     * @param boardId   읽을 객체 id
     * @param model
     * @return
     */
    @GetMapping("/read/{boardId}")
    public String read(@PathVariable Long boardId, Model model,
                       @ModelAttribute("criteria") Criteria criteria) {

        Board findBoard = boardRepository.findById(boardId);
        boardRepository.updateViewCount(boardId);

        List<Comment> commentList = commentRepository.findByBoardId(boardId);

        model.addAttribute(findBoard);
        model.addAttribute("commentList", commentList);

        return "/board/read";
    }

    /**
     * GET 으로 /write 요청 받아 /writeForm 호출
     * @param model
     * @return
     */
    @GetMapping("/write")
    public String writeForm(Model model) {
        // th:object 로 커맨드 객체 받기위해?, bindingResult 와 관련성?
        model.addAttribute("board", new Board());
        return "/board/writeForm";
    }

    /**
     * POST 로 /add 요청 받아 넘어온 BoardVO 를 db에 저장 후 /read/{boardId} 요청 리다이렉트
     * @param form   넘어온 폼객체, 템플릿 수정을 막기위해 Model 에 저장될 이름 지정.
     * @param redirectAttributes    리다이렉트 파라미터 추가
     * @return
     */
    @PostMapping("/write")
    public String writeBoard(@AuthenticationPrincipal PrincipalDetails principalDetails,
                        @Validated @ModelAttribute("board") BoardSaveForm form,
                        BindingResult bindingResult, HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {
        // 검증 오류 발견
        if (bindingResult.hasErrors()) {
            log.info("/write POST bindingResult.hasErrors");
            return "/board/writeForm";
        }

        // 현재 사용자 확인
        Member loginMember = principalDetails.getMember();

        // board 저장
        Board board = new Board(form.getTitle(), form.getWriter(), form.getContent());
        board.setMemberId(loginMember.getId());
        Board saveBoard = boardRepository.save(board);
        
        // 폼을 통해 실제로 들어온 이미지 확인
        String[] splittedImageName = form.getImageName().split(",");
        for (String imageName : splittedImageName) {
            log.info("/write imageName input by form = "+ imageName);
        }

        // 이미지 DB 저장(동기화)
        imageRepository.syncImage(saveBoard.getId(), splittedImageName);

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                "/board/read/" + saveBoard.getId(), "게시글이 등록되었습니다.", request.getQueryString()
        ));

        // POST, Post Redirect Get
        // 위에 따라서 /redirect:/board/read/{boardId} 엿는데 바뀜.
        return "redirect:/alert";
    }

    /**
     * GET /edit 으로 id 받아서 수정할 객체 찾고 Model 에 넣어서 /editForm 호출
     * @param boardId
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/edit/{boardId}")
    public String editForm(@PathVariable Long boardId, HttpServletRequest request, Model model,
                           RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal PrincipalDetails principalDetails,
                           @ModelAttribute Criteria criteria) {
        Board findBoard = boardRepository.findById(boardId);

        // 현재 로그인한 사람, 수정하려는 글의 작성자 비교
        if (isSameWriter(principalDetails, findBoard)) {
            model.addAttribute("board",findBoard);
            return "/board/editForm";
        }

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                "/board/read/" + boardId, "수정하려는 글과 작성자가 다릅니다.", request.getQueryString()
        ));
        return "redirect:/alert";
    }

    /**
     * POST /edit 으로 BoardVO 받아서 수정하고 id 넣어서 리다이렉트 (GET-POST 요청 동일하게)
     * @param boardId
     * @param form 넘어온 폼, 모델에 저장할 이름지정
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/edit/{boardId}")
    public String editBoard(@PathVariable Long boardId,@Validated @ModelAttribute("board") BoardEditForm form,
                       BindingResult bindingResult, HttpServletRequest request,
                       @ModelAttribute Criteria criteria,
                       RedirectAttributes redirectAttributes) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasErrors = {}", bindingResult);
            return "/board/editForm";
        }

        // Board 업데이트
        Board updateParam = new Board(
                form.getTitle(), form.getWriter(), form.getContent(), form.getRegedate());
       
        Board updateBoard = boardRepository.update(boardId, updateParam);

        // 들어온 이미지 확인
        String[] splittedImageName = form.getImageName().split(",");
        for (String imageName : splittedImageName) {
            log.info("/edit imageName input by form = "+ imageName);
        }

        // 이미지 동기화
        imageRepository.syncImage(updateBoard.getId(), splittedImageName);

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                "board/read/" + updateBoard.getId(), "게시글이 수정되었습니다.", request.getQueryString()));

        return "redirect:/alert";
    }

    /**
     * id 받아서 삭제
     * @param boardId
     * @return
     */
    @GetMapping("/delete/{boardId}")
    public String delete(@PathVariable Long boardId,
                         @AuthenticationPrincipal PrincipalDetails principalDetails,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(required = false) String currentPage) {
        Board findBoard = boardRepository.findById(boardId);

        if (!isSameWriter(principalDetails, findBoard)) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/board/read/" + boardId, "삭제하려는 글과 작성자가 다릅니다.", request.getQueryString()
            ));
            return "redirect:/alert";
        }

        int result = boardRepository.delete(boardId);
        if (result == 1) {log.info("boardRepository.delete({}) 성공", boardId);}

        // 댓글 삭제 delete cascade
        commentRepository.deleteByBoardId(boardId);

        // 이미지 삭제
        imageRepository.deleteImage(boardId);

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                "/board/list", "글이 삭제 되었습니다.", request.getQueryString()
        ));
        return "redirect:/alert";
    }

    /**
     * 글의 작성자와 세션에 등록된 현재사용자가 같은지 확인
     * @param principalDetails
     * @param findBoard
     * @return
     */
    public boolean isSameWriter(PrincipalDetails principalDetails, Board findBoard) {
        Member loginMember = principalDetails.getMember();
        return findBoard.getMemberId().equals(loginMember.getId());
    }



}
