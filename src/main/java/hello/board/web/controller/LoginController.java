package hello.board.web.controller;

import hello.board.domain.member.Member;
import hello.board.domain.service.LoginService;
import hello.board.web.form.LoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    // 로그인 로직을 실행할 loginService
    private final LoginService loginService;

    // /login GET
    @GetMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "/login/loginForm";
    }

    // /login POST
    // redirectURL: 인터셉터에서 로그인 처리 실패후 전달되는 요청URL, 로그인 후 해당 URL 로 리다이렉트한다.
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult,
                        HttpServletRequest request,
                        @RequestParam(value = "redirectURL", defaultValue = "/board/list") String redirectURL) throws UnsupportedEncodingException {

        // 세션 생성 및 미인증 사용자 요청 해제
        HttpSession session = request.getSession();
        session.setAttribute("isValidRequest", "true");

        // beanValidation 검증 오류 발견
        if (bindingResult.hasErrors()) {
            log.info("/login POST bindingResult.hasErrors");
            return "/login/loginForm";
        }

        // 로그인 처리
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        // 로그인 실패 (null)
        if (loginMember ==  null) {
            log.info("/login 로그인 실패");
            // 글로벌 오류 생성
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "/login/loginForm";
        }

        // 로그인 성공, 세션에 값 저장 (세션은 앞으로 요청시 request 객체에 계속 붙어서 나감)
        session.setAttribute("loginMember", loginMember);

        log.info("/login 로그인 성공, loginId = {}", loginMember.getLoginId());

        // 검색중 : queryString 에 파라미터 여러개 (option 포함)
        if (redirectURL.contains("option")) {
            // url 가공 및 인코딩
            log.info("/login input redirectURL = {}", redirectURL);

            String[] splitedURL = redirectURL.split("keyword=");
            String encodedKeyword = URLEncoder.encode(splitedURL[1], "UTF-8");
            redirectURL = (splitedURL[0] + "keyword=" + encodedKeyword).replace(".", "&");

        }

        log.info("/login output redirectURL = {}", redirectURL);
        return "redirect:" + redirectURL;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("/logout 로그아웃");
            // 세션 삭제
            session.invalidate();
        }
        return "redirect:/board/list";
    }


}
