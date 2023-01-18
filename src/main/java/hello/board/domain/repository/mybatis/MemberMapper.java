package hello.board.domain.repository.mybatis;

import hello.board.domain.member.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    void save(Member member);

    void update(@Param("id") Long id, @Param("updateParam") Member updateParam);

    void delete(Long id);

    Member findById(Long id);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByUsername(String username);

    List<Member> findAll();


}
