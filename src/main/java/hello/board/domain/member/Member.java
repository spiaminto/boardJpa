package hello.board.domain.member;

import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;

import static org.springframework.util.StringUtils.*;

@Getter @ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String loginId;     // UNIQUE
    private String username;    // UNIQUE
    private String password;

    private String email;
    private String emailVerified;   // 'true' or 'false', 'true' 인 경우 email 중복 불가. oauth 유저 = false
    private String role;

    private String provider;        // oauth provider 구분
    private String providerId;       // provider Pk (google = 최대 255자의 대소문자 구분 ASCII 문자 길이)

    private String picture;     // 일단 사용X

    public void updateMember(Member updateParam) {
        this.username = hasText(updateParam.getUsername()) ? updateParam.getUsername() : username;
        this.loginId = hasText(updateParam.getLoginId()) ? updateParam.getLoginId() : loginId;
        this.password = hasText(updateParam.getPassword()) ? updateParam.getPassword() : password;
        this.email = hasText(updateParam.getEmail()) ? updateParam.getEmail() : email;
        this.emailVerified = hasText(updateParam.getEmailVerified()) ? updateParam.getEmailVerified() : emailVerified;
    }

    public void updateOauth2MemberEmail(String email) {
        this.email = email;
    }

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
     *  password set 용.
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

    /**
     * 현재 Member 의 email 인증 여부를 설정
     */
     public void setVerified(boolean isVerified) {
         this.emailVerified = isVerified ? "true" : "false";
     }

     // isEmailVerified -> Mybatis Reflect 오류
     public boolean isVerified() {
         return "true".equals(this.emailVerified);
     }

}
