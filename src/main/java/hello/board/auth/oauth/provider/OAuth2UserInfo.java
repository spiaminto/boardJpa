package hello.board.auth.oauth.provider;

// OAuth2.0 제공자들 마다 응답해주는 속성값이 달라서 공통으로 만들어준다.
public interface OAuth2UserInfo {
	
	// 서비스 제공자
	String getProvider();

	// 서비스 제공자 pk
	String getProviderId();

	String getEmail();
	String getName();
}
