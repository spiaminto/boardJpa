package hello.board.web.controller;

import hello.board.domain.member.Member;
import hello.board.domain.repository.MemberRepository;
import hello.board.domain.repository.ResultDTO;
import hello.board.web.RedirectDTO;
import hello.board.web.form.MemberEditForm;
import hello.board.web.form.MemberSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberRepository memberRepository;

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

        Member member = new Member(
                form.getLoginId(), form.getUsername(), form.getPassword()
        );

        ResultDTO result = memberRepository.save(member);

        // 가입 실패
        if (!result.isSuccess()) {
            redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/member/add", result.getCustomMessage()));
            return "redirect:/alert";
        }

        redirectAttributes.addFlashAttribute("redirectDTO", new RedirectDTO("/board/list", "회원가입 완료"));

        return "redirect:/alert";
    }

    @GetMapping("/edit")
    public String editForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Member currentMember = (Member) session.getAttribute("loginMember");
        model.addAttribute("member", currentMember);
        return "/member/editForm";
    }

    @PostMapping("/edit")
    public String editMember(@Validated @ModelAttribute("member") MemberEditForm memberEditForm,
                             BindingResult bindingResult, Model model,
                             HttpServletRequest request, RedirectAttributes redirectAttributes
                             ) {
        // 검증 오류 발생
        if (bindingResult.hasErrors()) {
            log.info("/edit POST bindingResult.hasError");
            return "/member/editForm";
        }

        HttpSession session = request.getSession(false);
        Member currentMember = (Member) session.getAttribute("loginMember");
        String beforeLoginId = currentMember.getLoginId();
        String beforePassword = currentMember.getPassword();

        // Member 업데이트
        Member updateParam = new Member(
                memberEditForm.getLoginId(), memberEditForm.getUsername(), memberEditForm.getPassword()
        );

        log.info("currentMember.getId = {}", currentMember.getId());
        Member updatedMember = memberRepository.update(currentMember.getId(), updateParam);

        // 로그인 정보도 수정
        if (!beforeLoginId.equals(memberEditForm.getLoginId())
                || !beforePassword.equals(memberEditForm.getPassword())) {
            log.info("/edit POST 로그인 정보수정");

            // 세션삭제
            session.invalidate();

            // alert
            model.addAttribute("redirectDTO", new RedirectDTO(
                    "/login", "로그인 정보가 변경되었습니다. 재로그인해 주세요."));
            return "/alert";
        }

        // 이름만 수정
        // session 내부의 "loginMember" 는 위에서 update 하면 자동으로 반영됨. (객체 하나로 자동관리되는듯?)
        // session.setAttribute("loginMember", updatedMember);

        //alert
        model.addAttribute("redirectDTO", new RedirectDTO(
                "/board/list", "멤버 정보가 변경되었습니다."
        ));

        // 세션갱신
        session.setAttribute("loginMember", updatedMember);

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
