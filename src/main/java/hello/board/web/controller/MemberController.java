package hello.board.web.controller;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.domain.paging.PageMaker;
import hello.board.domain.repository.BoardRepository;
import hello.board.domain.repository.CommentRepository;
import hello.board.domain.repository.MemberRepository;
import hello.board.domain.repository.ResultDTO;
import hello.board.web.RedirectDTO;
import hello.board.web.auth.PrincipalDetails;
import hello.board.web.form.MemberEditForm;
import hello.board.web.form.MemberSaveForm;
import hello.board.web.form.OAuth2MemberEditForm;
import hello.board.web.form.OAuth2MemberSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;

    private final CommentRepository commentRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입 /add
    @GetMapping("/add")
    public String addMember(Model model) {
        model.addAttribute("member", new Member());
        return "/member/addForm";
    }

    @PostMapping("/add")
    public String addMember(@Validated @ModelAttribute("member") MemberSaveForm form,
                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/add POST bindingResult.hasError");
            return "/member/addForm";
        }

        String encodedPassword = bCryptPasswordEncoder.encode(form.getPassword());

        Member member = new Member(
                form.getLoginId(), form.getUsername(), encodedPassword, form.getEmail()
        );

        member.setRole("ROLE_USER");

        ResultDTO result = memberRepository.save(member);

        // 가입 실패
        if (!result.isSuccess()) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/member/add", result.getCustomMessage()));
            return "redirect:/alert";
        }

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/board/list/all", "회원가입 완료"));

        return "redirect:/alert";
    }

    @GetMapping("/add/oauth2")
    public String addOauth2Member(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                  RedirectAttributes redirectAttributes, Model model) {
        Member member = principalDetails.getMember();

        // 비정상 요청
        if (!member.getRole().equals("ROLE_TEMP")) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/board/list/all", "소셜가입_비정상 요청"
            ));
            return "redirect:/alert";
        }

        model.addAttribute("member", principalDetails.getMember());
        model.addAttribute("isOauth2", "true");

        return "/member/addForm";
    }

    @PostMapping("/add/oauth2")
        public String addOauth2Member(@Validated @ModelAttribute("member") OAuth2MemberSaveForm form,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails,
                                      RedirectAttributes redirectAttributes) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/add/oauth2 POST bindingResult.hasError {}", bindingResult);
            return "/member/addForm";
        }

        Member member = principalDetails.getMember();
        member.setRole("ROLE_USER");
        member.setUsername(form.getUsername());

        ResultDTO result = memberRepository.save(member);

        // 가입 실패
        if (!result.isSuccess()) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/member/add", result.getCustomMessage()));
            return "redirect:/alert";
        }

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/board/list/all", "소셜계정 회원가입 완료"));

        return "redirect:/alert";
    }

    @GetMapping("/mypage/info")
    public String infoForm(@AuthenticationPrincipal PrincipalDetails principalDetails,
                           Model model) {
        Long id = principalDetails.getMember().getId();
        model.addAttribute("member", memberRepository.findById(id));

        // OAuth2 로그인 유저인 경우
        if (principalDetails.getMember().getProvider() != null) {
            model.addAttribute("isOauth2", "true");
        }

        return "/member/infoForm";
    }

    @PostMapping("/edit")
    public String editMember(@Validated @ModelAttribute("member") MemberEditForm memberEditForm,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal PrincipalDetails principalDetails
                             ) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError");
            return "/member/infoForm";
        }

        Member currentMember = principalDetails.getMember();
        String beforeLoginId = currentMember.getLoginId();
        String beforeUsername = currentMember.getUsername();
        String beforePassword = currentMember.getPassword();

        String encodedPassword = bCryptPasswordEncoder.encode(memberEditForm.getPassword());

        // Member 업데이트
        Member updateParam = new Member(
                memberEditForm.getLoginId(), memberEditForm.getUsername(), encodedPassword, memberEditForm.getEmail()
        );

        log.info("currentMember.getId = {}", currentMember.getId());
        Member updatedMember = memberRepository.update(currentMember.getId(), updateParam);

        // username 수정 -> 동기화
        if (!beforeUsername.equals(memberEditForm.getUsername())) {
            boolean isSuccess = syncUserName(currentMember.getId(), beforeUsername, memberEditForm.getUsername(), redirectAttributes);

            // 실패하면 바로 리다이렉트
            if (!isSuccess) {
                return "redirect:/alert";
            }
        }

        // loginId, password 수정 -> 강제 로그아웃
        if (!beforeLoginId.equals(memberEditForm.getLoginId())
                || !bCryptPasswordEncoder.matches(memberEditForm.getPassword(), beforePassword)
        ) {
            log.info("/edit POST 로그인 정보수정");

            // alert
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/logout", "로그인 정보가 변경되었습니다. 재로그인해 주세요."));
            return "redirect:/alert";
        }

        // 시큐리티 세션 갱신 (보안상? 맞는진? 모르겠음)
        principalDetails.editMember(updatedMember.getUsername());

        //alert
        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                "/member/mypage/info", "멤버 정보가 변경되었습니다."
        ));

        return "redirect:/alert";
    }

    @PostMapping("/edit/oauth2")
    public String editOAuth2Member(@Validated @ModelAttribute("member")OAuth2MemberEditForm oAuth2MemberEditForm,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError");
            return "/member/infoForm";
        }

        Member currentMember = principalDetails.getMember();
        String username = oAuth2MemberEditForm.getUsername();
        String providerId = oAuth2MemberEditForm.getProviderId();

        log.info("providerId = {}, username = {}", providerId, username);

        boolean isSuccess = false;

        isSuccess = memberRepository.updateUsername(providerId, username) == 1 ? true : false;

        // 시큐리티 세션 갱신 (보안상? 맞는진? 모르겠음)
        principalDetails.editMember(username);

        if (isSuccess) {
                // username 수정 -> 동기화
                boolean isSyncSuccess = syncUserName(currentMember.getId(), currentMember.getUsername(), oAuth2MemberEditForm.getUsername(), redirectAttributes);

                // 실패하면 바로 리다이렉트
                if (!isSyncSuccess) {
                    return "redirect:/alert";
                }

            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/member/mypage/info", "멤버 정보가 변경되었습니다."
            ));
            return "redirect:/alert";

        } else {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/member/mypage/info", "멤버 정보 변경에 실패하였습니다."
            ));
            return "redirect:/alert";
        }
    }

    // 내 글
    @GetMapping({"/mypage/myboard/{categoryCode}", "/mypage/myboard"})
    public String myPage(@AuthenticationPrincipal PrincipalDetails principalDetails,
                         @ModelAttribute("criteria") Criteria criteria,
                         Model model) {

        Member currentMember = principalDetails.getMember();
        log.info(criteria.getCurrentPage() + criteria.getKeyword() + criteria.getOption() + criteria.getCategory());

        // 총 글갯수 가져오기
        Integer countTotalBoard = boardRepository.countTotalBoardWithMemberId(criteria, currentMember.getId());

        // 글 가져오기
        List<Board> pagedBoard = boardRepository.findPagedBoardWithMemberId(criteria, currentMember.getId());

        // 페이징 할 정보 설정하기
        PageMaker pageMaker = new PageMaker(criteria, countTotalBoard);

        // 페이지메이커, 글 목록 모델에 넣기
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("boardList", pagedBoard);


        return "/member/myBoard";
    }

    // 내 댓글
    @GetMapping({"/mypage/mycomment/{categoryCode}", "/mypage/mycomment"})
    public String myComment(@AuthenticationPrincipal PrincipalDetails principalDetails,
                         @ModelAttribute("criteria") Criteria criteria,
                         Model model) {

        Member currentMember = principalDetails.getMember();
        log.info(criteria.getCurrentPage() + criteria.getKeyword() + criteria.getOption() + criteria.getCategory());

        // 댓글 가져오기
        List<Comment> pagedComment = commentRepository.findPagedCommentWithMemberId(criteria, currentMember.getId());

        List<Long> commentIdList = new ArrayList<>();
        for (Comment comment : pagedComment) {
            commentIdList.add(comment.getCommentId());
        }

        List<Board> boardList = new ArrayList<>();
        if (!commentIdList.isEmpty()) {
            // 댓글이 달린 게시글 가져오기
            boardList = boardRepository.findByIdList(commentIdList);
        }


        // 총 글갯수 가져오기
        Integer countTotal = commentRepository.countTotalCommentWithMemberId(criteria, currentMember.getId());

        // 페이징 할 정보 설정하기
        PageMaker pageMaker = new PageMaker(criteria, countTotal);

        // 페이지메이커, 글 목록 모델에 넣기
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("commentList", pagedComment);
        model.addAttribute("boardList", boardList);

        return "/member/myComment";
    }

    @ResponseBody
    @GetMapping("/duplicateCheck")
    public boolean duplicateCheck(@RequestParam(value = "loginId", defaultValue = "") String loginId,
                                  @RequestParam(value = "username", defaultValue = "") String username) {
        log.info("request duplicateCHeck");
        log.info("loginId = {}", loginId);
        log.info("username = {}", username);

        if (loginId.equals("")) {
            return memberRepository.duplicateCheck("username", username);
        } else {
            return memberRepository.duplicateCheck("loginId",loginId);
        }
    }

    // username 동기화, 성공시 true 반환
    public boolean syncUserName(Long memberId, String currentUsername, String udpateUsername, RedirectAttributes redirectAttributes) {
        ResultDTO boardResult = boardRepository.syncWriter(memberId, udpateUsername);
        ResultDTO commentResult = commentRepository.syncWriterAndTarget(memberId, udpateUsername);

        // username 동기화 에러
        if (!boardResult.isSuccess() || !commentResult.isSuccess()) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO(
                    "/member/mypage/info", "시스템 문제로 닉네임을 바꾸는데 실패했습니다."
            ) );
            if (!boardResult.isSuccess()) {
                log.info("{} , {} -> {}, ResultDTO.exception = {}, ResultDTO.message = {}",
                        boardResult.getCustomMessage(), currentUsername, udpateUsername,
                        boardResult.getException(), boardResult.getMessage() );
            } else {
                log.info("{} , {} -> {}, ResultDTO.exception = {}, ResultDTO.message = {}",
                        commentResult.getCustomMessage(), currentUsername, udpateUsername,
                        commentResult.getException(), commentResult.getMessage() );
            }
            return false;
        }
        return true;
    }


    // 회원삭제 /delete/{id}
    // 회원갱신 /update/{id, boardVO}
    // 글 조회 /my-board/{writer}

}
