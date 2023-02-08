package hello.board.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Authentication 객체에서 role 이 ROLE_TEMP 인지 확인
 */

@Slf4j
public class TempUserCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean isTemp = request.isUserInRole("ROLE_TEMP");

        if (isTemp) {
                log.info("ROLE_TEMP 임시유저 요청");
                
                // ROLE_TEMP 이면 로그아웃 처리
                response.sendRedirect("/logout");    
        }

        return true;
    }
}
