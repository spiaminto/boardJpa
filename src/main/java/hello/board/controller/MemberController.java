package hello.board.controller;

import hello.board.auth.PrincipalDetails;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.domain.paging.PageMaker;
import hello.board.form.*;
import hello.board.repository.ResultDTO;
import hello.board.service.ImageService;
import hello.board.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;

    // 회원가입 /add
    @GetMapping("member/add")
    public String addMember(Model model) {
        model.addAttribute("member", Member.builder().build());
        return "member/addForm";
    }

    @PostMapping("member/add")
    public String addMember(@Validated @ModelAttribute("member") MemberSaveForm form,
                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/add POST bindingResult.hasError {}", bindingResult);
            return "member/addForm";
        }

        Member member = Member.builder()
                .loginId(form.getLoginId())
                .username(form.getUsername())
                .password(form.getPassword())
                .email(form.getEmail())
                .emailVerified(form.getEmailVerified())
                .role("ROLE_USER").build();

        ResultDTO result = memberService.addMember(member);

        // 가입 실패
        if (!result.isSuccess()) {
            redirectAttributes.addFlashAttribute("alertMessage", result.getCustomMessage());
            return "redirect:/member/add";
        }

        redirectAttributes.addFlashAttribute("alertMessage", "회원가입 성공");
        return "redirect:/login";
    }

    @GetMapping("/member/add-oauth")
    public String addOauth2Member(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                  RedirectAttributes redirectAttributes, Model model) {
        Member member = principalDetails.getMember();

        // 비정상 요청
        if (!member.getRole().equals("ROLE_TEMP")) {
            redirectAttributes.addFlashAttribute("alertMessage", "소셜가입 비정상 요청");
            return new UrlBuilder().redirectHome();
        }

        model.addAttribute("member", principalDetails.getMember());
        model.addAttribute("isOauth2", "true");

        return "member/addForm";
    }

    @PostMapping("member/add-oauth")
        public String addOauth2Member(@Validated @ModelAttribute("member") OAuth2MemberSaveForm form,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails,
                                      RedirectAttributes redirectAttributes) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/add/oauth2 POST bindingResult.hasError {}", bindingResult);
            return "member/addForm";
        }

        Member member = principalDetails.getMember();
        member.setOauth2ActualMember(form.getUsername());

        ResultDTO result = memberService.addMember(member);

        // 가입 실패
        if (!result.isSuccess()) {
            redirectAttributes.addFlashAttribute("alertMessage", result.getCustomMessage());
            return "redirect:/member/add-oauth";
        }

        redirectAttributes.addFlashAttribute("alertMessage", "소셜계정 회원가입 성공");
        return new UrlBuilder().redirectHome();
    }

    @GetMapping("/member/{memberId}")
    public String infoForm(@PathVariable Long memberId,
                           Model model) {
        Member findMember = memberService.findById(memberId);
        model.addAttribute("member", findMember);
        model.addAttribute("memberId", memberId);

        // OAuth2 로그인 유저인 경우
        if (findMember.getProvider() != null) {
            model.addAttribute("isOauth2", "true");
        }

        return "member/infoForm";
    }

    @PostMapping("/member/{memberId}/edit")
    public String editMember(@Validated @ModelAttribute("member") MemberEditForm memberEditForm,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal PrincipalDetails principalDetails,
                             @PathVariable Long memberId
                             ) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError {}", bindingResult);
            return "member/infoForm";
        }
        Member currentMember = memberService.findById(memberId);

        // MemberEditForm -> Member
        Member updateParam = Member.builder()
                .loginId(memberEditForm.getLoginId())
                .username(memberEditForm.getUsername())
                .password(memberEditForm.getPassword())
                .email(memberEditForm.getEmail())
                .emailVerified(memberEditForm.getEmailVerified())
                .build();

        // 멤버 수정
        Map<String, Object> resultMap = memberService.editMember(currentMember, updateParam);

        // syncUsername 실패
        if (resultMap == null) {
            redirectAttributes.addFlashAttribute("alertMessage", "시스템 문제로 닉네임을 바꾸는데 실패했습니다.");
            return new UrlBuilder("/member").id(memberId).buildRedirectUrl();
        }

        // loginId, password 수정 -> 강제 로그아웃
        if ((boolean) resultMap.get("isLogout")) {
            log.info("/edit isLogout = true");

            redirectAttributes.addFlashAttribute("alertMessage", "로그인 정보가 변경되었습니다. 다시 로그인 해 주세요");
            redirectAttributes.addFlashAttribute("isLogout", "true");
            return new UrlBuilder().redirectHome();
        }

        Member updatedMember = (Member) resultMap.get("updatedMember");
        // 시큐리티 세션 갱신 (보안상? 맞는진? 모르겠음)
        principalDetails.editMember(updatedMember.getUsername());

        //alert
        redirectAttributes.addFlashAttribute("alertMessage", "회원 정보가 변경되었습니다.");

        return new UrlBuilder("/member").id(memberId).buildRedirectUrl();
    }

    @PostMapping("/member/{memberId}/edit-oauth")
    public String editOAuth2Member(@Validated @ModelAttribute("member")OAuth2MemberEditForm oAuth2MemberEditForm,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails,
                                   @PathVariable Long memberId,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError");
            return "member/infoForm";
        }
        Member currentMember = memberService.findById(memberId);

        // oAuth2MemberEditForm -> Member

        Member updateParam = Member.builder()
                .username(oAuth2MemberEditForm.getUsername())
                .providerId(oAuth2MemberEditForm.getProviderId()).build();

        // 수정
        Map<String, Object> resultMap = memberService.editMember(currentMember, updateParam);

        // syncUsername 실패
        if (resultMap == null) {
            redirectAttributes.addFlashAttribute("alertMessage", "시스템 문제로 닉네임을 바꾸는데 실패했습니다.");
            return new UrlBuilder("/member").id(memberId).buildRedirectUrl();
        }

        Member updatedMember = (Member) resultMap.get("updatedMember");
        // 시큐리티 세션 갱신
        principalDetails.editMember(updatedMember.getUsername());

        redirectAttributes.addFlashAttribute("alertMessage", "회원 정보가 변경되었습니다.");
        return new UrlBuilder("/member").id(memberId).buildRedirectUrl();

    }

    @GetMapping("/member/find")
    public String findForm(Model model) {
        model.addAttribute("findForm", new FindForm());
        return "member/findForm";
    }

    @PostMapping("/member/find")
    public String findMember(@Validated @ModelAttribute("findForm") FindForm findForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if ("password".equals(findForm.getFindOption())) {
            // 비밀번호는 민감해서 한번 더 검증함.
            String newPassword = findForm.getPassword();
            if (newPassword == null || newPassword.length() < 4 || newPassword.length() > 16) {
                bindingResult.rejectValue("password", "비밀번호는 4자 이상 16자 이하로 입력해주세요.");
            }
        }
        if (bindingResult.hasErrors()) {
            log.info("/find POST bindingResult.hasError, bindingResult={}", bindingResult);
            return "member/findForm";
        }

        String message= memberService.findMember(findForm);

        redirectAttributes.addFlashAttribute("findMessage", message);
        return "redirect:/member/find";
    }

    // 내 글
    @GetMapping("/member/{memberId}/boards")
    public String myPage(@ModelAttribute("criteria") Criteria criteria,
                         @PathVariable Long memberId,
                         Model model) {

        Member currentMember = memberService.findById(memberId);
//        log.info(criteria.getCurrentPage() + criteria.getKeyword() + criteria.getOption() + criteria.getCategory());

        Map<String, Object> resultMap = memberService.myPage(criteria, currentMember.getId());

        // 페이징 할 정보 설정하기
        PageMaker pageMaker = new PageMaker(criteria, (int) resultMap.get("countTotalContent"));

        // 페이지메이커, 글 목록 모델에 넣기
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("boardList", resultMap.get("boardList"));
        model.addAttribute("memberId", memberId);

        return "member/myBoard";
    }

    // 내 댓글
    @GetMapping("/member/{memberId}/comments")
    public String myComment(@ModelAttribute("criteria") Criteria criteria,
                           @PathVariable Long memberId,
                           Model model) {

        Member currentMember = memberService.findById(memberId);
//        log.info(criteria.getCurrentPage() + criteria.getKeyword() + criteria.getOption() + criteria.getCategory());

        Map<String, Object> resultMap = memberService.myComment(criteria, currentMember.getId());

        // 페이징 할 정보 설정하기
        PageMaker pageMaker = new PageMaker(criteria, (int) resultMap.get("countTotalContent"));

        // 페이지메이커, 글 목록 모델에 넣기
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("commentList", resultMap.get("commentList"));
        model.addAttribute("boardList", resultMap.get("boardList"));
        model.addAttribute("memberId", memberId);

        return "member/myComment";
    }

    @PostMapping("/member/{memberId}/delete")
    public String deleteMember(@AuthenticationPrincipal PrincipalDetails principalDetails,
                               @PathVariable Long memberId,
                               RedirectAttributes redirectAttributes) {
        // pathVariable 로 들어온 memberId 는 위험할듯.
        Long currentId = principalDetails.getMember().getId();

        // 멤버삭제 -> 이미지 삭제 : 이미지 삭제에서 에러나면, 멤버정보가 없는 이미지가 남음. 이는 내가 나중에 처리가능
        // 이미지삭제 -> 멤버삭제 : 멤버삭제에서 오류나면, 이미지가 없는 멤버글만 잔뜩 남게됨.

        // 멤버 삭제
        boolean isMemberDeleted = memberService.deleteMember(currentId);

        if (isMemberDeleted) {
            // 이미지 삭제
            boolean isImageDeleted = imageService.deleteImageByMemberId(currentId);
            log.info("MemberController.deleteMember() isImageDeleted = {}", isImageDeleted);
        }

        // 보험
        principalDetails.getMember().setTempMember();

        redirectAttributes.addFlashAttribute("alertMessage", "회원 탈퇴 되었습니다.");
        redirectAttributes.addFlashAttribute("isLogout", "true");

        return new UrlBuilder().redirectHome();
    }

    @ResponseBody
    @GetMapping("/member/duplicate-check")
    public boolean duplicateCheck(@RequestParam(value = "loginId", defaultValue = "") String loginId,
                                  @RequestParam(value = "username", defaultValue = "") String username) {

        String option = loginId.equals("") ? "username" : "loginId";
        String param = option.equals("username") ? username : loginId;

        log.info("duplicateCheck(), loginId = {}, username = {}, option = {}, param = {}", loginId, username, option, param);

        return memberService.duplicateCheck(option, param);
    }

    @ResponseBody
    @GetMapping("/member/verify-email")
    public String verifyEmail(@RequestParam(value = "email") String email,
                              @RequestParam(value = "option") String option,
                              HttpServletResponse response) {
        log.info("verifyEmail(), email = {}", email);

        String encodedVerifyCode = memberService.verifyEmail(email, option);
        if (encodedVerifyCode.length() != 60) {
            // verifyCode 생성 실패
            return encodedVerifyCode;
        }

        Cookie verifyCodeCookie = new Cookie("verifyCode", encodedVerifyCode);
        verifyCodeCookie.setMaxAge(300); // 유효시간 300초(5분)
        response.addCookie(verifyCodeCookie);

        return encodedVerifyCode != null ? "true" : "false";
    }

    @ResponseBody
    @GetMapping("/member/confirm-email")
    public boolean confirmEmail(@RequestParam String verifyCode,
                                HttpServletRequest request, HttpServletResponse response) {
        String encodedVerifyCode = "";
        boolean isVerified = false;

        // cookie 에서 (encoded)verifyCode 가져오기
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> verifyCookieOptional = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("verifyCode")).findFirst();

        if (verifyCookieOptional.isPresent()) {
            // verifyCode 쿠키가 존재
            Cookie verifyCookie = verifyCookieOptional.get();
            encodedVerifyCode = verifyCookie.getValue();

            // client 에서 받은 verifyCode 와 cookie 에서 가져온 encodedVerifyCode 를 비교
            isVerified = memberService.confirmEmail(verifyCode, encodedVerifyCode);

            if (isVerified) {
                // 인증됨 -> cookie 삭제
                verifyCookie.setMaxAge(0);
                response.addCookie(verifyCookie);
            }
        } else {
            isVerified = false;
        }
        log.info("confirmEmail(), verifyCode = {}, encodedVerifyCode = {}", verifyCode, encodedVerifyCode);
        return isVerified;
    }
}
