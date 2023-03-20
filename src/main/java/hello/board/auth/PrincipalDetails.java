package hello.board.auth;

import hello.board.domain.member.Member;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
@Slf4j
@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final Member member;
    
    // OAuth2User.getAttributes() 로 받은 정보
    private final Map<String, Object> attributes;

    public PrincipalDetails(Member member) {
        this.member = member;
        this.attributes = null;
    }

    // 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    // 유저네임 변경 (private)
    private void setUsername(String username) { member.setOauth2Username(username); }

    // 유저 정보 변경 등의 이유로 PrincipalDetails 의 Member 를 갱신해야할때 (현재는 username 만)
    public void editMember(String username) { setUsername(username); }

    public String getEmail() {
        return member.getEmail();
    }

    // OAuth2User, user PK
    @Override
    public String getName() {
        return member.getId() + "";
    }

    // OAuth2User
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
