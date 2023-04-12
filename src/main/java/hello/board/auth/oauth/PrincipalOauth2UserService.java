package hello.board.auth.oauth;

import hello.board.auth.PrincipalDetails;
import hello.board.auth.oauth.provider.GoogleUserInfo;
import hello.board.auth.oauth.provider.KakaoUserInfo;
import hello.board.auth.oauth.provider.NaverUserInfo;
import hello.board.auth.oauth.provider.OAuth2UserInfo;
import hello.board.domain.member.Member;
import hello.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
/**
 * OAuth2 유저 인증 처리 클래스
 */
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    /**
     * OAuth2UserRequest 를 가공한 뒤 Authentication 객체에 저장할 멤버 정보를 담은 OAuth2User 반환
     * (PrincipalDetails implements Oauth2User)
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
//        log.info("OAuth2User.loadUser() oAuth2User={}", oAuth2User);

        return processOAuth2User(userRequest, oAuth2User);
    }

    /**
     * super.loadUser() 로 불러온 OAuth2유저 정보를 provider 에 따라 구분하여 가공 후
     * 해당 멤버가 DB 에 존재하면 로그인
     * 해당 멤버가 DB 에 존재하지 않으면 회원가입
     * 을 위해 OAuth2User 를 기반으로 PrincipalDetails 생성 후 반환.
     * 
     * 가공/처리 분리 예정
     */
    private OAuth2User processOAuth2User (OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = null; // OAuth2유저 정보
        Member returnMember = null; // 반환할 멤버
        
        // provider 에 따라 구분하여 정보생성
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            log.info("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            log.info("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));

        } else {
            log.info("지원하지 않는 로그인 요청");
        }

        // DB 에 OAuth2 유저 존재여부 조회
        Optional<Member> findMember =
                memberRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());

        String forOauth2UserLoginId = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();
        String forOauth2UserUsername = oAuth2UserInfo.getEmail().substring(0, oAuth2UserInfo.getEmail().indexOf('@')); //email 에서 @gmail.com 제외한 나머지

        if (findMember.isPresent()) {
            log.info("OAuth2 유저 로그인 {} - {} ", oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());
            returnMember = findMember.get();

            // 이메일 바뀌면 갱신후 재조회
            if (!oAuth2UserInfo.getEmail().equals(findMember.get().getEmail())) {
                log.info("OAuth2 유저 이메일 변경 {} - {} ", oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());
                memberRepository.updateEmail(oAuth2UserInfo.getProviderId(), oAuth2UserInfo.getEmail());
                returnMember = memberRepository.findByLoginId(returnMember.getLoginId()).get();
            }
            
        } else {
            log.info("OAuth2 유저 회원가입, OAuth2UserInfo={}", oAuth2UserInfo);

            // 반환용 임시멤버 생성
            returnMember = Member.builder()
                    .provider(oAuth2UserInfo.getProvider())
                    .providerId(oAuth2UserInfo.getProviderId())
                    .loginId(forOauth2UserLoginId)
                    .username(forOauth2UserUsername)
                    .password("forOauth2UserPassword")
                    .email(oAuth2UserInfo.getEmail())
                    .role("ROLE_TEMP").build();
        }
        return new PrincipalDetails(returnMember, oAuth2User.getAttributes());
    }

}
