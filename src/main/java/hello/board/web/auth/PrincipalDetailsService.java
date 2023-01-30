package hello.board.web.auth;

import hello.board.domain.member.Member;
import hello.board.domain.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public PrincipalDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // loadUserByUsername 이지만, loginId 를 통해 로그인 하도록 함.
    // input name=username 이 자동으로 바인딩 (또는 SecurityConfig 에서 .usernamePatameter("param") 으로 변경가능)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("PrincipalDetailsService.loadUserByUsername({})", username);
        Member member = memberRepository.findByLoginId(username).orElse(null);

        // 비밀번호는 자동으로 매치여부를 확인한다 (읽을거리 참고)

        if (member == null) {
            return null;
        } else {
            // 유저가 있으면, principalDetails 객체에 넣어서 반환
            return new PrincipalDetails(member);
        }
    }

}
