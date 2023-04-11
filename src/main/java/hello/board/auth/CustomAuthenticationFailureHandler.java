package hello.board.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SimpleUrlAuthenticationFailureHandler 를 상속하여 SpringSecurity 일반 로그인 실패 처리
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{

    /**
     * authentication 중 예외가 발생한 request 를 받아 에러메시지를 설정하고,
     * 에러메시지와 로그인정보를 request 객체에 파라미터로 설정한 후, 포워딩
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String loginId = request.getParameter("loginId");

        String errorMessage;

        //error.properties 의 Spring-Security 에러메시지 참조
        if (exception instanceof BadCredentialsException) {
            // 자격 증명 실패
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";
        } else if (exception instanceof UsernameNotFoundException) {
            // ID 를 찾을 수 없음
            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
        } else {
            errorMessage = "로그인에 실패하였습니다 관리자에게 문의하세요.";
        }

        setDefaultFailureUrl("/loginForm?error=true");
        setUseForward(true);

        // url 에 노출하지 않음.
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("loginId", loginId);

        super.onAuthenticationFailure(request, response, exception);
    }
}
