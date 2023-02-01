package hello.board.config;

import hello.board.web.auth.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터를 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true)  // @Secured 어노테이션 사용 및 활성화
@RequiredArgsConstructor
// 스프링 시큐리티 필터
public class SecurityConfig {

    private final AuthenticationFailureHandler customFailureHandler;
    private final PrincipalOauth2UserService principalOauth2UserService;

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
                .antMatchers("/*/write/**", "/*/edit/**", "/*/delete/**", "/*/info/**").authenticated()

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

                .logoutSuccessUrl("/board/list")

                .and()

                .oauth2Login()

                // 딱히 의미 업음? (로그인 폼을 provider 가 제공)
                .loginPage("/loginForm")

                .defaultSuccessUrl("/board/list")

                // code 전송 부분을 자동화 하고, access token + 사용자 프로필 정보를 바로 받는다.
                .userInfoEndpoint()

                // 이 정보를 통한 후처리는 DefaultOauth2UserService principalOauth2UserService.loadUser() 에서 담당
                .userService(principalOauth2UserService);

        return http.build();
    }

    // logout 후 login할 때 정상동작을 위함
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
