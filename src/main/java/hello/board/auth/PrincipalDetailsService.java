package hello.board.auth;

import hello.board.domain.member.Member;
import hello.board.repository.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
/**
 * SpringSecurity 일반 유저 인증 처리 클래스
 */
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberJpaRepository memberRepository;

    /**
     * Member.loginId 를 통해 인증 후 UserDetails 반환
     * (PrincipalDetails implements UserDetails)
     * @param username Member.username 가 아니라 Member.loginId 를 파라미터로 받음.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.info("loadByUsername, username={}", username);
        Optional<Member> findMember = memberRepository.findByLoginId(username);

        if (findMember.isPresent()) {
            return new PrincipalDetails(findMember.get());
        } else {
            log.info("PrincipalDatailsService.loadUserByUsername() member == null, username(loginId)={}", username);
            throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다. username=" + username);
        }
    }

}
