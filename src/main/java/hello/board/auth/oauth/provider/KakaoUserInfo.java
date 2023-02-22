package hello.board.auth.oauth.provider;

import lombok.ToString;

import java.util.Map;

@ToString
public class KakaoUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes;
	private Map<String, Object> kakaoAccount;
	private Map<String, Object> kakaoProfile;

	
    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");
    }
	
    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    // kakao 는 name X nickname O
    @Override
    public String getName() {
        return (String) kakaoProfile.get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccount.get("email");
    }

	@Override
	public String getProvider() {
		return "kakao";
	}
}

/*
    참고: 카카오 attributes 구조
    Name: [2648082678],
    Granted Authorities: [[ROLE_USER, SCOPE_account_email, SCOPE_profile_nickname]],
    User Attributes: [{ id=2648082678,
                        connected_at=2023-02-02T09:08:39Z,
                        properties={ nickname=spiaminto },
                        kakao_account={ profile_nickname_needs_agreement=false,
                                        profile={nickname=spiaminto},
                                        has_email=true,
                                        email_needs_agreement=false,
                                        is_email_valid=true,
                                        is_email_verified=true,
                                        email=spiaminto@gmail.com
                                        }
                     }]
*/
