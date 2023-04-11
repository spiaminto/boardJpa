package hello.board.repository.mybatis;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    Integer countTotalBoard(Criteria criteria);
    Integer countTotalBoardWithMemberId(@Param("criteria") Criteria criteria, @Param("memberId") Long memberId);

    void updateViewCount(long id);

    Board findById(Long id);

    List<Board> findPagedBoard(Criteria criteria);
    List<Board> findPagedBoardWithMemberId(@Param("criteria") Criteria criteria, @Param("memberId") Long memberId);

    int save(Board board);

    int update(@Param("id") Long id, @Param("updateParam") Board updateParam);

    int syncWriter(@Param("memberId") Long memberId, @Param("updateName") String updateName);

    int update(long viewCount);

    int delete(Long id);
}
