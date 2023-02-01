package hello.board.domain.repository;

import hello.board.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    ResultDTO save(Member member);

    Member update(Long id, Member updateParam);

    int updateEmail(String providerId, String email);

    int updateUsername(String providerId, String username);

    void delete(Long id);

    Member findById(Long id);

    Optional<Member> findByLoginId(String loginId);

    List<Member> findAll();

    boolean duplicateCheck(String option, String param);

    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

}
