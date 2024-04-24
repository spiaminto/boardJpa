package hello.board.repository;

import hello.board.domain.member.Member;
import hello.board.repository.jpa.MemberJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class zMemberJpaOnlyRepositoryTest {

//    @Autowired
//    MemberJpaOnlyRepository memberRepository;

    @Autowired
    MemberJpaRepository memberRepository;

    @Autowired
    EntityManager em;

    Member member = Member.builder()
            .loginId("testLog").username("testName").password("1111").email("email@email.com").emailVerified("false").build();

    @Test
    public void saveTest() {
        memberRepository.save(member);
        Member findMember = memberRepository.findById(member.getId()).orElse(null);
        assertThat(findMember.equals(member));
    }

    @Test
    public void updateTest() {
        memberRepository.save(member);

        // updateByForm
        Member updateParam = Member.builder().loginId("updateLo").username("updateName").password("2222").email("updateemail@email.com").emailVerified("true").build();
//        memberRepository.updateMember(member.getId(), updateParam);
        member.updateMember(updateParam);
        em.flush();
//        Member findMember = memberRepository.findById(member.getId());
        assertThat(member.getLoginId().equals(updateParam.getLoginId()));
        assertThat(member.getUsername().equals(updateParam.getUsername()));
        assertThat(member.getPassword().equals(updateParam.getPassword()));
        assertThat(member.getEmail().equals(updateParam.getEmail()));
        assertThat(member.getEmailVerified().equals(updateParam.getEmailVerified()));

        //updateOnlyUsername
        Member updateParam2 = Member.builder().username("updateName2").build();
        member.updateMember(updateParam2);
        em.flush();
        assertThat(member.getLoginId().equals(updateParam.getLoginId()));
        assertThat(member.getUsername().equals(updateParam.getUsername()));
        assertThat(member.getPassword().equals(updateParam.getPassword()));
        assertThat(member.getEmail().equals(updateParam.getEmail()));
        assertThat(member.getEmailVerified().equals(updateParam.getEmailVerified()));

    }

//    @Test
//    public void deleteTest() {
//        memberMapper.save(member);
//        memberMapper.delete(member.getId());
//        Member findMember = memberMapper.findById(member.getId());
//        assertThat(findMember==null);
//    }
//
//    @Test
//    public void findByIdTest() {
//        memberMapper.save(member);
//        Member findMember = memberMapper.findById(member.getId());
//        assertThat(findMember.equals(member));
//    }
//
//    @Test
//    public void findAlltest() {
//        List<Member> all = memberMapper.findAll();
//        log.info(all.toString());
//    }
//
//    @Test
//    public void findByLoginIdTest() {
//        memberMapper.save(member);
//        Optional<Member> findMember = memberMapper.findByLoginId(member.getLoginId());
//        assertThat(findMember.equals(member));
//
//    }

}
