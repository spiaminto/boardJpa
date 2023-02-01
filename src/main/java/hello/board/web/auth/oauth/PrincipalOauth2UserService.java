package hello.board.web.auth.oauth;

import hello.board.domain.member.Member;
import hello.board.domain.repository.MemberRepository;
import hello.board.web.auth.PrincipalDetails;
import hello.board.web.auth.oauth.provider.FaceBookUserInfo;
import hello.board.web.auth.oauth.provider.GoogleUserInfo;
import hello.board.web.auth.oauth.provider.NaverUserInfo;
import hello.board.web.auth.oauth.provider.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    // Authentication 에 저장하기 위해 OAuth2User 객체 리턴
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 유저정보 받아와서
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2User.loadUser() oAuth2User={}", oAuth2User);

        // provider 에 맞게 처리
        return processOAuth2User(userRequest, oAuth2User);
    }

    // loadUser() 에서 로드한 유저를 provider 에 따라 구분하여 처리
    private OAuth2User processOAuth2User (OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // 유저 정보를 담을 객체 (내부에서만 사용)
        OAuth2UserInfo oAuth2UserInfo = null;
        
        // provider 에 따라 구분하여 정보생성
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            log.info("페북 로그인 요청");
            oAuth2UserInfo = new FaceBookUserInfo(oAuth2User.getAttributes());

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            log.info("네이버 로그인 요청");
            // 네이버는 Map 안에 response ={...} 에 attributes 가 들어있다.
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));

        } else {
            log.info("지원하지 않는 로그인 요청");
        }

        // 해당 OAuth2User 의 provider 와 providerId 로 DB 에서 유저 찾기
        Optional<Member> findMember =
                memberRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());

        Member member = null;

        String forOauth2UserLoginId = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();
        // 초기 username = email 에서 @gmail.com 제외한 나머지
        String forOauth2UserUsername = oAuth2UserInfo.getEmail().substring(0, oAuth2UserInfo.getEmail().indexOf('@'));

        // 찾은 유저가 존재
        if (findMember.isPresent()) {
            log.info("이미 존재하는 Oauth2User");
            member = findMember.get();
            // 이메일 바뀌면 이메일 갱신
            if (!oAuth2UserInfo.getEmail().equals(member.getEmail())) {
                log.info("{} 이메일 변경 {} -> {}, providerId={}",
                        oAuth2UserInfo.getName(), oAuth2UserInfo.getEmail(), member.getEmail(), oAuth2UserInfo.getProviderId());
                memberRepository.updateEmail(oAuth2UserInfo.getProviderId(), oAuth2UserInfo.getEmail());
            }

        // 찾은 유저 없음
        } else {
            log.info("OAuth2User 회원가입, OAuth2UserInfo={}", oAuth2UserInfo);
            // 회원가입
            // 비밀번호 암호화 하지 않음 -> 로그인 불가능
            member = new Member(oAuth2UserInfo.getProvider(),
                    oAuth2UserInfo.getProviderId(),
                    forOauth2UserLoginId,
                    forOauth2UserUsername,
                    "forOauth2UserPassword",
                    oAuth2UserInfo.getEmail(),
                    "ROLE_USER" );
            memberRepository.save(member);
        }

        // 온전한 멤버정보의 리턴을 위해 조회후 리턴
        findMember = memberRepository.findByLoginId(forOauth2UserLoginId);

        return new PrincipalDetails(findMember.get(), oAuth2User.getAttributes());
    }
}
