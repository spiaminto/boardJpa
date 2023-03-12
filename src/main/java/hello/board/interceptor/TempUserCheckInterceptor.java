package hello.board.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication 객체에서 role 이 ROLE_TEMP 인지 확인
 */

@Slf4j
public class TempUserCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean isTemp = request.isUserInRole("ROLE_TEMP");

        if (isTemp) {
                log.info("ROLE_TEMP 임시유저 요청, requestUri = {}", request.getRequestURI());
                
                // ROLE_TEMP 이면 로그아웃 처리
                response.sendRedirect("/logout");    
        }

        return true;
    }
}
