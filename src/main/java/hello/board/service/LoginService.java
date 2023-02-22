package hello.board.service;

import hello.board.domain.member.Member;
import hello.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

//@Service
@RequiredArgsConstructor
// 로그인 로직
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * 로그인 처리
     * @param loginId
     * @param password
     * @return memberVO, orElse(null)
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(member -> member.getPassword().equals(password))
//                Optional 객체가 null 일때 반환할 값
                .orElse(null);
    }
    

}
