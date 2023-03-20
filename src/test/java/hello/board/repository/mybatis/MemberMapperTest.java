package hello.board.repository.mybatis;

import hello.board.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
public class MemberMapperTest {

    // 테스트 이외에 @Autowired 사용하지 않도록 한다.
    @Autowired
    MemberMapper memberMapper;

    Member member = Member.builder().loginId("testLog").username("testName").password("1111").email("email@email.com").build();

    @Test
    public void saveTest() {
        memberMapper.save(member);
        Member findMember = memberMapper.findById(member.getId());
        assertThat(findMember.equals(member));
    }

    @Test
    public void updateTest() {
        memberMapper.save(member);
        Member updateParam = Member.builder().loginId("updateLo").username("updateNAme").password("2222").email("email@email.com").build();
        memberMapper.update(member.getId(), updateParam);
        Member findMember = memberMapper.findById(member.getId());
        assertThat(findMember.getLoginId().equals(updateParam.getLoginId()));
        assertThat(findMember.getUsername().equals(updateParam.getUsername()));
        assertThat(findMember.getPassword().equals(updateParam.getPassword()));
    }

    @Test
    public void deleteTest() {
        memberMapper.save(member);
        memberMapper.delete(member.getId());
        Member findMember = memberMapper.findById(member.getId());
        assertThat(findMember==null);
    }

    @Test
    public void findByIdTest() {
        memberMapper.save(member);
        Member findMember = memberMapper.findById(member.getId());
        assertThat(findMember.equals(member));
    }

    @Test
    public void findAlltest() {
        List<Member> all = memberMapper.findAll();
        log.info(all.toString());
    }

    @Test
    public void findByLoginIdTest() {
        memberMapper.save(member);
        Optional<Member> findMember = memberMapper.findByLoginId(member.getLoginId());
        assertThat(findMember.equals(member));

    }











}
