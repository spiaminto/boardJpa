package hello.board.web.interceptor;

import hello.board.web.RedirectDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

// webConfig.class 에서 등록
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 요청 URI 확인
        String requestURI = request.getRequestURI();
        // 요청 파라미터 확인
        String currentPage = request.getParameter("currentPage");
        // 쿼리스트링
        String queryString =request.getQueryString();

        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        HttpSession session = request.getSession();
        
        if (session == null || session.getAttribute("loginMember") == null) {
            log.info("미인증 사용자 요청");

            session.setAttribute("isValidRequest", "false");

            // 쿼리 파라미터 없음
            if (queryString == null) {
                response.sendRedirect("/login?redirectURL=" + requestURI);
                return false;
            }

            // 접속 하려 한 주소를 파라미터로 붙여 redirect (로그인 시 해당 페이지로 재이동을 위해)
            // 검색 옵션 존재
            if (queryString.contains("option")) {
                queryString = queryString.replace("&", ".");
                log.info("replace = {}", queryString);
                response.sendRedirect("/login?redirectURL=" + requestURI + "?" + queryString);
            // 검색 옵션 없음 (currentPage 1개)
            } else {
                response.sendRedirect("/login?redirectURL=" + requestURI + "?" + queryString);
            }

            // 인터셉터 체인 종료
            return false;
        }
        // 인터셉터 체인 실행
        return true;
    }
}
