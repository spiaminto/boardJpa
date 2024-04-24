package hello.board.repository.jpa;

import hello.board.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    @Query("select m from Member m where m.email = :email and m.emailVerified = 'true'")
    Optional<Member> findByEmail(String email);
}