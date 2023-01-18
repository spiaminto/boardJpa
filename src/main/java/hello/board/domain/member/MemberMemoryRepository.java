package hello.board.domain.member;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemberMemoryRepository {

    private static Map<Long, Member> memberStore = new HashMap<>();
    private static long sequence = 0L;

    //save
    public Member save(Member member) {
        member.setId(++sequence);
        memberStore.put(member.getId(), member);
        return member;
    }

    // findById
    public Member findById(Long id) {
        return memberStore.get(id);
    }

    // findByLoginId (optional)
    // loginId 검증용 메서드. nullable 하므로 Optional 사용
    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(memberVO -> memberVO.getLoginId().equals(loginId))
                // loginId 중복불가
                .findFirst();
    }

    // findAll
    public List<Member> findAll() {
        return new ArrayList<>(memberStore.values());
    }

    // update
    public Member update(Long id, Member updateParam) {
        Member findMember = memberStore.get(id);
        findMember.setUsername(updateParam.getUsername());
        findMember.setLoginId(updateParam.getLoginId());
        findMember.setPassword(updateParam.getPassword());
        return findMember;
    }

    // delete
    public Member delete(Long id) {
        return memberStore.remove(id);
    }

    // clearstore
    public void clearStore() {
        memberStore.clear();
    }


}
