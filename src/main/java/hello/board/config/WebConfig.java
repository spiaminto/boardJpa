package hello.board.config;

import hello.board.interceptor.HttpLogInterceptor;
import hello.board.interceptor.TempUserCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// WebMvcConfigurer: Spring Interceptor 사용을 위해 구현

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 이미지 요청 url
    private String resourceRequestUrl = "/local/image/**";
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

        registry.addInterceptor(new HttpLogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/css/**",
                        "https://maxcdn.bootstrapcdn.com/**", "/js/ckeditor5/**", "/static-image/**");

        registry.addInterceptor(new TempUserCheckInterceptor())
                .order(2)
                .addPathPatterns("/board/**", "/member/**", "/image/**", "/comment/**")
                //                      체크하고     홈보내고       추가함
                .excludePathPatterns("/login/check", "/boards", "/member/add-oauth", "/member/duplicate-check");
    }

}
