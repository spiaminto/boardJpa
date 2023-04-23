package hello.board.repository.mybatis;

import hello.board.domain.member.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    int save(Member member);

    int update(@Param("id") Long id, @Param("updateParam") Member updateParam);

    int updateEmail(@Param("providerId") String providerId, @Param("email") String email);

    int updateUsername(@Param("providerId") String providerId, @Param("username") String username);

    int delete(Long id);

    Member findById(Long id);
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByUsername(String username); // 중복유저 체크시 사용
    Optional<Member> findByProviderAndProviderId(@Param("provider")String provider, @Param("providerId") String providerId);
    Optional<Member> findByEmail(String email); // 이메일 인증시 사용

    List<Member> findAll();


}
