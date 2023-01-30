package hello.board.web.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

// SimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler 클래스 상속
// 예외 처리 후 setDefualtFailureUrl(String url) 로 세팅한 url 로 forward

// https://dev-coco.tistory.com/126 참고,

// spring security 일반 로그인 실패 핸들러 (실패 메시지 출력용)

/**
 * 생각해볼점
 *  레퍼런스에서는 로그인 실패 시 id 입력을 유지하지 않음
 *  id 입력 유지를 위해 여기서 id 를  전송해줄 필요가 있는데, SimpleUrlAuth.. 는 구조상 redirect 가 기본
 *  따라서 redirect 할 url 에 직접 파라미터 붙여줘야 하는데 id 를 붙이기는 좀 그럼.
 *  그래서 forward 로 바꿔줄 수 있는데 그러면 클라단에서 url 변경이 안됨 (/loginProc 노출)
 *  id 노출보다는 낫다고 생각해서 그냥 포워딩으로 함.
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // loginId 가져옴
        String loginId = request.getParameter("loginId");
        // null
        String queryString = request.getQueryString();

        // queryString 은 없는데 파라미터는 가져옴?
//        log.info("queryString = {}, loginID = {}", queryString, loginId);

        String errorMessage;

        // 비밀번호가 일치하지 않음 (아이디 틀림이랑 구분하면 보안성 떨어진다고 함)
        if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요.";

        // 존재하지 않는 아이디
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";

        // 시스템 문제로 인증 처리가 불가능?
        } else if (exception instanceof InternalAuthenticationServiceException) {
//            errorMessage = "시스템 내부 문제로 인증 처리가 불가능 합니다.";
            // 정황상 아래가 맞는듯?
            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";

        // 인증 요구가 거부됨
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";

        } else {
            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }
        
        // 에러 여부는 url 뒤에 붙임 (레퍼런스는 여기다가 메시지도 붙임)
        setDefaultFailureUrl("/loginForm?error=true");

        // redirect 대신 forward 사용
        setUseForward(true);

        // 에러 메시지와 loginId 는 request 객체에 붙임 (각각 너무 긺, id 노출싫어서)
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("loginId", loginId);

        // 스프링 시큐리티 기본 처리로 이어서 진행
        super.onAuthenticationFailure(request, response, exception);
    }
}
