package hello.board.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// webConfig.class 에서 등록
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 요청 URI 확인
        String requestURI = request.getRequestURI();
        // 요청파라미터 (쿼리스트링) 확인
        String queryString =request.getQueryString();

        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        HttpSession session = request.getSession();

        // 미인증 사용자 요청, 들어온 uri + queryString 해서 sendRedirect()
        if (session == null || session.getAttribute("loginMember") == null) {
            session.setAttribute("isValidRequest", "false");

            // 쿼리 파라미터 없음
            if (queryString == null) {
                response.sendRedirect("/login?redirectURL=" + requestURI);
                log.info("미인증 사용자 처리 완료, URI = {}", requestURI);
                
                // 인터셉터 종료
                return false;
            }

            // 쿼리 파라미터 있음
            //  검색 옵션 있음 (option, keyword, currntPage)
            if (queryString.contains("option")) {
                
                // & 기준으로 url 이 구성되므로, 붙일 파라미터는 & 가 아닌 다른문자로치환
                queryString = queryString.replace("&", ".");
                log.info("replace = {}", queryString);

                response.sendRedirect("/login?redirectURL=" + requestURI + "?" + queryString);
            //   검색 옵션 없음 (currentPage 1개)
            } else {
                response.sendRedirect("/login?redirectURL=" + requestURI + "?" + queryString);
            }

            log.info("미인증 사용자 처리 완료, URI = {}, QueryString ={}", requestURI, queryString);

            // 인터셉터 체인 종료
            return false;
        }
        // 인터셉터 체인 실행
        return true;
    }
}
