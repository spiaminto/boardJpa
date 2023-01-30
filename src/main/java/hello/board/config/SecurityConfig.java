package hello.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터를 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true)  // @Secured 어노테이션 사용 및 활성화
@RequiredArgsConstructor
// 스프링 시큐리티 필터
public class SecurityConfig {

    private final AuthenticationFailureHandler customFailureHandler;

    // 패스워드 암호화 인코더 등록
    @Bean
    public BCryptPasswordEncoder pwEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // SecurityFilterChain 을 리턴하는 메서드를 @Bean 으로 등록 -> component 방식으로 스프링 컨테이너가 관리
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // 요청 인증 방식 설정
        http.authorizeRequests()

                // 로그인 필수
                .antMatchers("/*/write/**", "/*/edit/**", "/*/delete/**").authenticated()

                // 일단 로그아웃 하지말고
                //.antMatchers("/logout").authenticated()

                // 모두 허용
                .anyRequest().permitAll()

                .and()

                .formLogin()

                // input name = loginId
                .usernameParameter("loginId")
                
                // GET /login, loginForm 불러오는 요청 커스텀
                .loginPage("/loginForm")
                
                // POST /login, 로그인 처리부 요청 커스텀. 해당url 로그인 요청은 UserDetailsService 로 처리
                .loginProcessingUrl("/loginProc")

                .failureHandler(customFailureHandler)

                .defaultSuccessUrl("/board/list")

                .and()

                .logout().permitAll()

                .logoutUrl("/logout")

                .logoutSuccessUrl("/board/list");

        /*
                .and()

                .oauth2Login()

                // 딱히 의미 업음? (로그인 폼을 provider 가 제공)
                .loginPage("/login")

                .userInfoEndpoint()
                ;
        */
        return http.build();
    }
}
