package hello.board;

import hello.board.web.interceptor.LogInterceptor;
import hello.board.web.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// WebMvcConfigurer: Spring Interceptor 사용을 위해 구현

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 이미지 요청 url
    private String resourceRequestUrl = "/images/request/**";
    // 이미지 로컬 요청 경로
    private String resourceLocation = "file:///C:/Users/felix/.Study/Spring_Practice/board_exfile/";

    // 이미지 업로드 시 요청되는 url 을 로컬 요청으로 변경
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourceRequestUrl)
                .addResourceLocations(resourceLocation);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/images/**", "/css/**", "/js/**", "/image/**",
                        "/comment/**",
                        "/test/**",
                        "/alert",
                        "/board/list", "/board/read/*",
                        "/member/add", "/member/duplicateCheck",
                        "/logout", "/login",
                        "/error",
                        "/reset.css", "/favicon.ico",
                        "https://maxcdn.bootstrapcdn.com/**");

        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**",
                        "https://maxcdn.bootstrapcdn.com/**");
    }

}
