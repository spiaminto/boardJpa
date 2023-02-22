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

    void delete(Long id);

    Member findById(Long id);

    Optional<Member> findByLoginId(String loginId);

    // 중복유저 체크할떄 사용
    Optional<Member> findByUsername(String username);
    Optional<Member> findByProviderAndProviderId(@Param("provider")String provider, @Param("providerId") String providerId);

    List<Member> findAll();


}
