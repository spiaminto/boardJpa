package hello.board.controller;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.enums.Category;
import hello.board.domain.paging.PageMaker;
import hello.board.auth.PrincipalDetails;
import hello.board.form.BoardEditForm;
import hello.board.form.BoardSaveForm;
import hello.board.service.BoardService;
import hello.board.service.ImageService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
// final, @NonNull 필드만 받는 생성자
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ImageService imageService;


    /*
    url 패턴 ( {variable}: 변수, [...]: 비 필수 )
    /board /{action} /{criteria.categoryCode} /[{board.Id}] ? currentPage=0 [ &option=option &keyword=keyword ]
     */

    @GetMapping("/")
    public String index() {
        return "redirect:/board/list/all";
    }

    @GetMapping(value = {"/board/list/{categoryCode}", "/board/list"})
    public String pagedList(Model model, @ModelAttribute("criteria") Criteria criteria) {

        // 글 가져오기
        List<Board> pagedBoard = boardService.findPagedBoard(criteria);

        // 총 글갯수 가져오기
        Integer countTotalBoard = boardService.countTotalBoard(criteria);

        // 페이징 할 정보 설정하기
        PageMaker pageMaker = new PageMaker(criteria, countTotalBoard);

        // 페이지메이커, 글 목록 모델에 넣기
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("boardList", pagedBoard);

        return "board/list";
    }

    @GetMapping("/board/read/{categoryCode}/{boardId}")
    public String read(@PathVariable Long boardId, Model model,
                       @AuthenticationPrincipal PrincipalDetails principalDetails,
                       @RequestParam(required = false, value = "selected") String commentId,
                       @ModelAttribute("criteria") Criteria criteria) {

        Map result = boardService.readBoard(boardId);

        model.addAttribute("board", result.get("board"));
        model.addAttribute("commentList", result.get("commentList"));

        if (commentId != null) {
            model.addAttribute("selectedCommentId", commentId);
        }

        // comment 에서 member.username 사용해야됨.
        if (principalDetails != null) {
            model.addAttribute("member", principalDetails.getMember());
        }

        return "board/read";
    }

    @GetMapping("/board/write/{categoryCode}")
    public String writeForm(Model model, @ModelAttribute Criteria criteria) {
        // th:object 로 커맨드 객체 받기위해?, bindingResult 와 관련성?
        model.addAttribute("board", new Board());
        return "board/writeForm";
    }

    @PostMapping("/board/write/{categoryCode}")
    public String writeBoard(@AuthenticationPrincipal PrincipalDetails principalDetails,
                        @Validated @ModelAttribute("board") BoardSaveForm form,
                        BindingResult bindingResult, HttpServletRequest request,
                        @ModelAttribute("criteria") Criteria criteria,
                        RedirectAttributes redirectAttributes) {

        if (form.getCategory() == Category.NOTICE && !request.isUserInRole("ROLE_ADMIN")) {
            bindingResult.rejectValue("category", "Unauthorized.category");
        }
        // 검증 오류 발견
        if (bindingResult.hasErrors()) {
            log.info("/write POST bindingResult.hasErrors {}", bindingResult);
            return "board/writeForm";
        }

        // form -> board
        Board board = new Board(form.getTitle(), form.getWriter(), principalDetails.getMember().getId(),
                form.getContent(), form.getCategory(), LocalDateTime.now());
        
        // 저장
        Board savedBoard = boardService.saveBoard(board);
        
        // 폼을 통해 실제로 들어온 이미지 확인
        String[] splittedImageName = form.getImageName().split(",");
//        for (String imageName : splittedImageName) {
//            log.info("/write imageName input by form = "+ imageName);
//        }

        // 이미지 동기화
        imageService.syncImage(savedBoard.getMemberId(), savedBoard.getId(), splittedImageName);

        redirectAttributes.addFlashAttribute("alertMessage", "게시글이 등록되었습니다.");

        // POST, Post Redirect Get
        return new UrlBuilder("/board/read/" + criteria.getCategoryCode())
                .id(savedBoard.getId()).queryString(request.getQueryString()).buildRedirectUrl();
    }

    @GetMapping("/board/edit/{categoryCode}/{boardId}")
    public String editForm(@PathVariable Long boardId, HttpServletRequest request, Model model,
                           RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal PrincipalDetails principalDetails,
                           @ModelAttribute Criteria criteria) {
        Board findBoard = boardService.findById(boardId);

        // 현재 로그인한 사람, 수정하려는 글의 작성자 비교
        if (boardService.isSameWriter(principalDetails.getMember(), findBoard)) {
            model.addAttribute("board",findBoard);
            return "board/editForm";
        }

        redirectAttributes.addFlashAttribute("alertMessage", "수정하려는 글과 작성자가 다릅니다.");

        return new UrlBuilder("/board/read/" + criteria.getCategoryCode())
                .id(boardId).queryString(request.getQueryString()).buildRedirectUrl();
    }

    @PostMapping("/board/edit/{categoryCode}/{boardId}")
    public String editBoard(@PathVariable Long boardId,@Validated @ModelAttribute("board") BoardEditForm form,
                       BindingResult bindingResult, HttpServletRequest request,
                       @ModelAttribute Criteria criteria,
                       RedirectAttributes redirectAttributes) {

        if (form.getCategory() == Category.NOTICE && !request.isUserInRole("ROLE_ADMIN")) {
            bindingResult.rejectValue("category", "Unauthorized.category");
        }

        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasErrors = {}", bindingResult);
            return "board/editForm";
        }

        log.info("board.categoryString = {}, criteria.categoryString = {}", form.getCategory(), criteria.getCategoryCode());

        // form -> board 맵핑
        Board updateParam = new Board(
                form.getTitle(), form.getWriter(), form.getContent(), LocalDateTime.now(), form.getCategory());
       
        // 업데이트
        Board updateBoard = boardService.updateBoard(boardId, updateParam);

        // 들어온 이미지 확인
        String[] splittedImageName = form.getImageName().split(",");
//        for (String imageName : splittedImageName) {
//            log.info("/edit imageName input by form = "+ imageName);
//        }

        // 이미지 동기화
        imageService.syncImage(updateBoard.getMemberId(), updateBoard.getId(), splittedImageName);

        redirectAttributes.addFlashAttribute("alertMessage", "게시글이 수정되었습니다.");

        return new UrlBuilder("/board/read/" + criteria.getCategoryCode())
                .id(updateBoard.getId()).queryString(request.getQueryString()).buildRedirectUrl();
    }

    @GetMapping("/board/delete/{categoryCode}/{boardId}")
    public String delete(@PathVariable Long boardId,
                         @AuthenticationPrincipal PrincipalDetails principalDetails,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes,
                         @ModelAttribute Criteria criteria) {
        Board findBoard = boardService.findById(boardId);

        if (!boardService.isSameWriter(principalDetails.getMember(), findBoard)) {
            redirectAttributes.addFlashAttribute("alertMessage", "삭제하려는 글과 작성자가 다릅니다.");
            return new UrlBuilder("/board/read/" + criteria.getCategoryCode())
                    .id(boardId).queryString(request.getQueryString()).buildRedirectUrl();

        }

        // board 삭제
        int result = boardService.deleteBoard(boardId);
        log.info("삭제된 글 {}개, id = {}", result, boardId);
        
        // 이미지 삭제 ( if result == 1 )
        boolean isSuccess = imageService.deleteImageByBoardId(boardId);
        log.info("이미지 삭제 isSuccess = {}", isSuccess);

        redirectAttributes.addFlashAttribute("alertMessage", "글이 삭제 되었습니다.");

        return new UrlBuilder().redirectHome();
    }





}
