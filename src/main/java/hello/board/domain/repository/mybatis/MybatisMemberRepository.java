package hello.board.domain.repository.mybatis;

import hello.board.domain.member.Member;
import hello.board.domain.repository.MemberRepository;
import hello.board.domain.repository.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@Transactional
public class MybatisMemberRepository implements MemberRepository {

    private final MemberMapper memberMapper;

    public MybatisMemberRepository(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }


    @Override
    public ResultDTO save(Member member) {
        try {
            memberMapper.save(member);
        } catch(DuplicateKeyException e) {
            return new ResultDTO(false, e.toString(), e.getMessage(), "로그인ID 와 이름 은 중복을 허용하지 않습니다.");
        }
        return new ResultDTO(true);
    }


    @Override
    public Member update(Long id, Member updateParam) {
        memberMapper.update(id, updateParam);
        return memberMapper.findById(id);
    }

    @Override
    public int updateEmail(String providerId, String email) {
        return memberMapper.updateEmail(providerId, email);
    }

    @Override
    public int updateUsername(String providerId, String username) {
        return memberMapper.updateUsername(providerId, username);
    }

    @Override
    public void delete(Long id) {
        memberMapper.delete(id);
    }

    @Override
    public Member findById(Long id) {
        return memberMapper.findById(id);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    @Override
    public List<Member> findAll() {
        return memberMapper.findAll();
    }

    @Override
    public Optional<Member> findByProviderAndProviderId(String provider, String providerId) {
        return memberMapper.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public boolean duplicateCheck(String option, String param) {
        Optional<Member> result;
        if (option.equals("username")) {
            result = memberMapper.findByUsername(param);
        } else {
            result = memberMapper.findByLoginId(param);
        }
        return !result.isEmpty();
    }
}
