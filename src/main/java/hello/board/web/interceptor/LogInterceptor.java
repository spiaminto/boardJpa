package hello.board.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        Enumeration<String> parameterNames = request.getParameterNames();
        String queryString = request.getQueryString();


        log.info(" REQUEST URI={}", requestURI);
        log.info(" REQUEST QueryString={}", queryString);

        // 다음 인터셉터 진행
        return true;
    }
}
