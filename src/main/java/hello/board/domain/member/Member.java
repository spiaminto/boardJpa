package hello.board.domain.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Member {

    private Long id;

    private String loginId;

    private String username;

    private String password;

    // for google oauth2
    private String email;
    private String picture;

    // oauth provider 구분, google
    private String provider;

    // google Pk (최대 255자의 대소문자 구분 ASCII 문자 길이)
    private String providerId;

    //security 를 위해 임시생성
    private String role;

    // save, normal Edit
    public Member(String loginId, String username, String password, String email) {
        this.loginId = loginId;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Member(String loginId, String username, String password, String email, String role) {
        this.loginId = loginId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // security 위해 만든 임시 생성자, 강사는 @Builder 통한 Builder패턴 사용
    public Member(String provider, String providerId, String forOauth2UserLoginId, String forOauth2UserUsername, String password, String email, String role) {
        this.provider = provider;
        this.providerId = providerId;
        this.loginId = forOauth2UserLoginId;
        this.username = forOauth2UserUsername;
        this.password = password;
        this.email = email;
        this.role = role;

    }

}
