package hello.board.domain.member;

import lombok.*;

@Getter @ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Member {

    private Long id;
    private String loginId;     // UNIQUE
    private String username;    // UNIQUE
    private String password;

    // for google oauth2
    private String email;
    private String role;

    private String provider;        // oauth provider 구분
    private String providerId;       // provider Pk (google = 최대 255자의 대소문자 구분 ASCII 문자 길이)

    private String picture;     // 일단 사용X

    /**
     * Oauth2 Temp Member 를 정식 가입용 Member 로 교체
     */
    public void setOauth2ActualMember(String username) {
        this.role = "ROLE_USER";
        this.username = username;
    }

    /**
     * Oauth2 Member 의 username 을 set(변경)
     */
    public void setOauth2Username(String username) {
        this.username = username;
    }

    /**
     * 암호화된 password set 용.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 현재 Member 의 권한을 Temp 로 교체 (현재는 탈퇴 보험용)
     */
    public void setTempMember() {
        this.role = "ROLE_TEMP";
    }



}
