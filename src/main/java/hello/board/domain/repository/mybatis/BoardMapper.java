package hello.board.domain.repository.mybatis;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// mybatis mapper
@Mapper
public interface BoardMapper {

    Integer countTotalBoard(Criteria criteria);
    Integer countTotalBoardWithMemberId(@Param("criteria") Criteria criteria, @Param("memberId") Long memberId);

    void updateViewCount(long id);

    Board findById(Long id);

    List<Board> findByIdList(@Param("idList") List<Long> idList);

    // 검색용으로 사용
//    List<Board> findAll(BoardSerachCond cond);

    // 검색 + 페이징 된 board 찾기
    List<Board> findPagedBoard(Criteria criteria);
    List<Board> findPagedBoardWithMemberId(@Param("criteria") Criteria criteria, @Param("memberId") Long memberId);

    // 검색 + 페이징 + 카테고리
    List<Board> findPagedAndCategorizedBoard(Criteria criteria);

//    List<Board> findByWriter(String writer);

    int save(Board board);

    int update(@Param("id") Long id, @Param("updateParam") Board updateParam);

    int update(long viewCount);

    int delete(Long id);
}
