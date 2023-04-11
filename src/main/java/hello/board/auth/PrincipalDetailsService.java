package hello.board.auth;

import hello.board.domain.member.Member;
import hello.board.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/**
 * SpringSecurity 일반 유저 인증 처리 클래스
 */
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public PrincipalDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Member.loginId 를 통해 인증 후 UserDetails 반환
     * (PrincipalDetails implements UserDetails)
     * @param username Member.username 가 아니라 Member.loginId 를 파라미터로 받음.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.info("loadByUsername, username={}", username);
        Member member = memberRepository.findByLoginId(username).orElse(null);

        if (member == null) {
            return null;
        } else {
            return new PrincipalDetails(member);
        }
    }

}
