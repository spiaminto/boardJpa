package hello.board.repository;

import hello.board.domain.member.Member;
import hello.board.repository.mybatis.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class MemberRepository {

    private final MemberMapper memberMapper;

    public MemberRepository(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public Member findById(Long id) {
        return memberMapper.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberMapper.findByUsername(username);
    }

    public Optional<Member> findByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    public Optional<Member> findByProviderAndProviderId(String provider, String providerId) {
        return memberMapper.findByProviderAndProviderId(provider, providerId);
    }

    public ResultDTO save(Member member) {
        try {
            int rowNum = memberMapper.save(member);
            log.info("save(), row = {}", rowNum);
        } catch(DuplicateKeyException e) {
            return new ResultDTO(false, e.toString(), e.getMessage(), "로그인ID 와 이름 은 중복을 허용하지 않습니다.");
        }
        return new ResultDTO(true);
    }


    public int update(Long id, Member updateParam) {
        return memberMapper.update(id, updateParam);
    }

    public int updateEmail(String providerId, String email) {
        return memberMapper.updateEmail(providerId, email);
    }

    public int updateUsername(String providerId, String username) {
        return memberMapper.updateUsername(providerId, username);
    }

    public int delete(Long id) {
        return memberMapper.delete(id);
    }

}
