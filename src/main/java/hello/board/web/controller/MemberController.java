package hello.board.web.controller;

import hello.board.domain.member.Member;
import hello.board.domain.repository.MemberRepository;
import hello.board.domain.repository.ResultDTO;
import hello.board.web.RedirectDTO;
import hello.board.web.auth.PrincipalDetails;
import hello.board.web.form.MemberEditForm;
import hello.board.web.form.MemberSaveForm;
import hello.board.web.form.OAuth2MemberEditForm;
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

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberRepository memberRepository;

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

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/board/list", "회원가입 완료"));

        return "redirect:/alert";
    }

    @GetMapping("/info")
    public String infoForm(@AuthenticationPrincipal PrincipalDetails principalDetails,
                           Model model) {
        model.addAttribute("member", principalDetails.getMember());

        // OAuth2 로그인 유저인 경우
        if (principalDetails.getMember().getProvider() != null) {
            model.addAttribute("isOauth2", "true");
        }

        return "/member/infoForm";
    }

    @PostMapping("/edit")
    public String editMember(@Validated @ModelAttribute("member") MemberEditForm memberEditForm,
                             BindingResult bindingResult, Model model,
                             @AuthenticationPrincipal PrincipalDetails principalDetails
                             ) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError");
            return "/member/infoForm";
        }

        Member currentMember = principalDetails.getMember();
        String beforeLoginId = currentMember.getLoginId();
        String beforePassword = currentMember.getPassword();

        String encodedPassword = bCryptPasswordEncoder.encode(memberEditForm.getPassword());

        // Member 업데이트
        Member updateParam = new Member(
                memberEditForm.getLoginId(), memberEditForm.getUsername(), encodedPassword, memberEditForm.getEmail()
        );

        log.info("currentMember.getId = {}", currentMember.getId());
        Member updatedMember = memberRepository.update(currentMember.getId(), updateParam);

        // 로그인 정보도 수정 -> 강제 로그아웃
        if (!beforeLoginId.equals(memberEditForm.getLoginId())
                || !bCryptPasswordEncoder.matches(memberEditForm.getPassword(), beforePassword)
        ) {
            log.info("/edit POST 로그인 정보수정");

            // alert
            model.addAttribute("redirectDTO", new RedirectDTO(
                    "/logout", "로그인 정보가 변경되었습니다. 재로그인해 주세요."));
            return "/alert";
        }

        // 시큐리티 세션 갱신 (보안상? 맞는진? 모르겠음)
        principalDetails.editMember(updatedMember.getUsername());

        //alert
        model.addAttribute("redirectDTO", new RedirectDTO(
                "/board/list", "멤버 정보가 변경되었습니다."
        ));

        return "/alert";
    }

    @PostMapping("/edit/OAuth2")
    public String editOAuth2Member(@Validated @ModelAttribute("member")OAuth2MemberEditForm oAuth2MemberEditForm,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails,
                                   BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError");
            return "/member/infoForm";
        }

        String username = oAuth2MemberEditForm.getUsername();
        String providerId = oAuth2MemberEditForm.getProviderId();

        memberRepository.updateUsername(providerId, username);

        // 시큐리티 세션 갱신 (보안상? 맞는진? 모르겠음)
        principalDetails.editMember(username);

        //alert
        model.addAttribute("redirectDTO", new RedirectDTO(
                "/board/list", "멤버 정보가 변경되었습니다."
        ));
        return "/alert";
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



    // 회원삭제 /delete/{id}
    // 회원갱신 /update/{id, boardVO}
    // 글 조회 /my-board/{writer}

}
