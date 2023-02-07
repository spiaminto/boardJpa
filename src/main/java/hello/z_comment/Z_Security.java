package hello.z_comment;

public class Z_Security {
    /*
   
class SecurityConfigDemo 선언부
ㅇ 스프링 시큐리티 의존성 에 다른 변화
    
    /login (/logout) 요청을 스프링이 기본적으로 직접 처리함 -> 기본 /login 뷰로 이동
        ㄴ SecurityConfig 작성하면 비활성화
    
    로그인 되지 않는 모든 사용자에 대해 기본적으로 모든 url 접근불가
        기본유저는 user, 비번은 콘솔에 뜸.
        
    csrf 토큰 없는 데이터 요청 차단
    
    회원가입시 패스워드를 인코딩 하지 않으면 로그인처리가 안됨


중단점X
ㅇ XSS, csrf

    XSS: 글 내용같은 곳에 javascript 심어서 공격 (글 내용에 <script> 작성)

    csrf: 허가되지 않는 url 요청을 통한 공격
        ex) Get .../admin/point?id=user1&point=1000 주소를 href 등으로 박아놓고 관리자에게 클릭 유도
            ROLE 등 보안이 설정되어있어도 관리자가 누르면 뚫림
            => Post 로 막거나, 요청마다 유효한지를 판단하는 토큰을 삽입하여 막음(form 태그의 hidden)
            
            해당 토큰을 csrf 토큰이라고 부르고, 서버에서 이 토큰이 같이 온 요청이 아니면 차단
            
     스프링 시큐리티에서 기본적으로 csrf 를 막으므로, 테스트단(기능구현안됨)에서는 csrf.disabled() 로 해제


@Bean BcryptPasswordEncoder 등록부
ㅇ BcryptPasswordEncoder

    회원가입할때 password="1234" 로 하면, 스프링시큐리티에서 로그인 처리를 안해줌
    패스워드를 암호화 해야 처리해준다고 함.
    패스워드를 암호화 처리해주는 인코더를 SecurityConfig 클래스에서 @Bean 으로 등록
    (IOC 로 등록된다고 하는데 찾아보니 제어역전=IOC 였음)

    강사는 들어온 패스워드를 bCryptPasswordEncoder(rawPassword) 로 인코딩 해서 @entity 의 password 에 덮어씌움
        -> DB 에 rawPassword 저장하지 않음.encyrptedPassword 저장


class PrincipalDetails 클래스 선언부
ㅇ 시큐리티 로그인 처리와 PrincipalDetails 클래스

    해당 강의에서는 /config/auth 패키지 아래에 PrincipalDetais implements UserDetails 클래스를 만드는데,
    
    스프링 시큐리티에서 기본적으로 로그인을 처리한 뒤 session 에 유저 정보를 저장하는데
        일반적인 session 이 아니고 같은 session 이지만 다른, 전용 security session 을 사용한다고 함.(이름은 불명확)

        이 security session 에 유저정보를 저장할때는 반드시 Authentication 타입으로 저장하며,
            Authentication 객체 내부에 유저 정보를 UserDetails 타입으로 저장한다고 함.
            (아마도 Authentication 객체의 필드로 UserDetails userDetails 로 존재하는듯?)

        따라서 우리가 거기에 맞춰서 UserDetails 객체로 유저정보를 받아야 하는데,
        해당 강의에서는 UserDetails interface 를 구현한 PrincipalDetails 클래스를 직접 만들어서 사용하는듯.

        +) 추가
            security session?
                강의에서는 계속 security session 이라고 하길래 찾아보니
                스프링 시큐리티의 인 메모리 저장소인 SecurityContextHolder 라는 곳이 정확한 명칭인듯 하다.

                (강의에서는 httpSession 내부에 spring security 가 관리하는 공간이라고 설명함)

                SecurityContextHolder 내부의 SecurityContext 내부에 Authentication 이 저장되고
                Authentication 내부에 UserDetails 가 저장되는 구조.

                UserDetails 를 저장 한 후, 유저 세션 ID(JSessionId) 를 생성해 쿠키(?) 에 넣어 응답을 보내며,
                이후 쿠키(?) 에 붙은 JSessionId 를 통해 요청을 확인하고 인증하게 된다.



@EnableGlobalMethodSecurity 어노테이션 선언부
ㅇ enableGlobalMethodSecurity(securedEnabled=true)
    메서드 단에서 스프링 시큐리티를 적용하는 어노테이션. config 에서 선언한다.
    옵션으로
        securedEnabled = true -> @Secured 어노테이션 활성화
            컨트롤러의 메서드에 @Secured(role) 붙이면 해당 role 권한을 가진 유저만 접근 가능 나머지 접근거부

        prePostEnabled = true -> @PreAuthorize, @PostAuthorize 어노테이션 활성화
            컨트롤러에 메서드에 @PreAuthorize(role expression) 붙이면 해당 권한 가진 유저만 접근가능
            @Secured 와 파라미터 구조가 다름. 여러개를 집어넣을수 있다는게 다른듯?



    ex)
    @Secured("ROLE_ADMIN") or @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @RequestMapping("/admin")
    public String adminPage() { ... return adminPage; }
    
    @Secured 는 ROLE_ADMIN 하나만 거르고 @PreAuthorize 는 ROLE_MANAGER, ROLE_ADMIN 두개 거름(허용)
    
    이때 security session 의 Authentication 의 UserDetails principalDetails 의 getAuthorites() 로 권한검사
    하여 거르는 듯.
    

SecurityConfigDemo .oauth2Login() 및 이하설정
ㅇ google cloud oauth api 승인된 리디섹션 uri 와 로그인 및 사용자 정보 접근 ****(확실치 않음)****

    사용자가 로그인 요청하면 이걸 oauth 를 통해 구글로 위임 후,

        1. 구글에서 로그인 승인되면 구글로 부터 '구글로그인 인증 code' 가 승인된 리디렉션 uri 로 전송(요청) 되며,

        2. 해당 인증code 를 통해 유효한 구글 정보가 확인된 유저의 개인정보를 요청하기 위해
            '내'가 인증code 를 구글에 보내 access token 을 받는다. (개인 정보 요청 가능한 권한 토큰)

        3. 해당 access token 을 다시 구글에 요청하여 사용자의 구글 개인정보를 받아

        4. '내 서버'의 회원가입을 처리하거나, 로그인을 처리한다. (후처리)
            이때, 회원가입에 더 필요한 정보 (vip 등급, 집주소 등 google 에 없는정보) 가 있으면
            추가 정보입력창을 띄워 받아 처리한다.

    이때, http://localhost:8080/login/oauth2/code/google 이라는 예시에서,
        oauth2 Client 라이브러리? 를 사용하면 /login 아래의 주소가 고정이며, 해당 주소 요청을
        라이브러리에서 자동으로 처리해 준다.
        이때의 처리란, 로그인이 승인되어 전달된 구글의 'code' 를 받아 다시 구글 서버로 'access token' 을
        요청하고, 'access token' 을 받아, 사용자의 개인정보에 접근하는 과정을 의미하는듯.

    주의할 점은 로그인 시 "내" 가 로그인을 처리하는것이 아닌, 구글의 "/oauth2/authorization/google" 요청
    주소를 통해 사용자가 직접 구글페이지에서 로그인을 하는것이다.
    (해당 주소의 버튼등을 누르면 구글페이지로 리디렉션 되도록 '내'가 맵핑, 주소는 oauth2 라이브러리 고정)

    그런데, oauth2Client 라이브러리의 기능 중, 2~3번을 자동화 하는 기능이 존재한다.
    즉, 인증code 반환 및 access token 요청반환 및 개인정보 요청 을 '내' 단에서 따로 처리하지 않고 바로
        access token + 사용자 프로필(개인) 정보 를 사용가능하게 해주는 기능이 있다.
    해당 기능은 SecurityConfigDemo 의 .userInfoEndPoint() 이하 설정에 기록


    <그림, 틀릴수도 있으니 틀렸으면 적극적으로 바꿀것>
        
                    로그인 요청            로그인 위임              로그인 처리
        사용자 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ>  '나'의 서버 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ> 구글서버
                                                            인증 code 전송
         인증code 는 구글로그인 '인증'을 의미        <ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
                                                 access token 요청
         access token 은 '나' 의                 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ>
           사용자로 부터 위임받은 '권한' 을 의미                 access token 부여
                                                <ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
         '나' 의 '권한' 으로 구글이 가진          access token 으로 개인정보 요청
           개인정보가 응답되면 그 개인정보로         ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ>
                                                  access token 에 따른 개인정보 응답
         '나' 의 서버(DB) 에 회원가입 <ㅡㅡㅡ       <ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
           또는 로그인 처리
    

없음
ㅇ oauth2 로그인 처리와 spring security 로그인 처리의 통합

          spring security                                    oath2

        UserDetailsService                           DefaultOauth2UserService
   .loadUserByUsername (String username)          .loadUser (OAuth2UserRequest userRequest)
        return UserDetails                           return OAuth2User

             +) 서비스 종료후, @AuthenticationPrincipal 어노테이션 활성화

    각 서비스에서 리턴된 유저정보 객체는 SecurityContextHolder-SecurityContext-Authentication
    내부에 저장된다 (이하 security session)

    현재 예시 프로젝트에서는 UserDetails 를 PrincipalDetails로, UserDetailsService 를 PrincipalDetailsService로,
                                                   DefaultOauth2UserService 를 PrincipalOauth2UserService로
    구현 해 놓았다.

    두 로그인을 통합 관리하기 위해, 두 유저 정보객체 (UserDetails principalDetails, OAuth2User user) 를
    통합할 필요가 있다. 이를 위해 UserDetails 의 구현체인 PrincipalDetails 에 Oauth2User 도 구현하여
    결과적으로 PrincipalDetails 가 양쪽 모두를 구현하게 하면, 두 타입 모두 처리 할 수 있게된다.

        +) 로그인 통합 이유?
            로그인 통합을 하지 않으면, 로그인 방식 (spring security 이하 일반, oauth2) 에 따라 
            security session 저장되는 유저객체가 달라져 불러오는 방식이 달라짐 (indexController 참고)
            따라서 하나로 합쳐놓고, 로그인방식 상관없이 유저 정보를 사용하면 편리



ㅇ 읽을거리

    UserDetails.loadUserByUSername() 에서 비밀번호를 어떻게 검사하는 걸까?
    https://github.com/HomoEfficio/dev-tips/blob/master/Spring%20Security%EC%9D%98%20%EC%82%AC%EC%9A%A9%EC%9E%90%20%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8%20%EA%B2%80%EC%82%AC.md

    컨트롤러에서 AuthenticationManager.authenticate(Authentication)을 호출하면 스프링 시큐리티에 내장된 AuthenticationProvider의 authenticate() 메서드가 호출되는데, 이 중에서 DaoAuthenticationProvider.additionalAuthenticationChekcs(UserDetails, UsernamePasswordAuthenticationToken) 메서드에 다음과 같은 코드가 있다.

    String presentedPassword = authentication.getCredentials().toString();

		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}

























     */
}
