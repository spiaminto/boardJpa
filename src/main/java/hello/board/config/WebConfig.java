package hello.board.config;

import hello.board.interceptor.HttpLogInterceptor;
import hello.board.interceptor.TempUserCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfigurer 구현체.
 * 인터셉터 등록과 리소스 요청경로 수정에 사용됨.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private String resourceRequestUrl = "/local/image/**"; // 브라우저에서 이미지 요청 url
    private String resourceLocation = "file:///C:/Users/felix/.Study/Spring_Practice/board_exfile/"; // 이미지 로컬 요청 경로

    /**
     * 브라우저에서 (로컬)이미지 요청시 url 을 로컬 요청으로 변경
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourceRequestUrl)
                .addResourceLocations(resourceLocation);
    }

    /**
     * 인터셉터 등록
     */
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
