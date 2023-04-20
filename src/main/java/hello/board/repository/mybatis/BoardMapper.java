package hello.board.repository.mybatis;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.repository.BoardCommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    Integer countTotalBoard(Criteria criteria);
    Integer countTotalBoardWithMemberId(@Param("criteria") Criteria criteria, @Param("memberId") Long memberId);

    void updateViewCount(long id);
    void addCommentCnt(Long id);
    void subtractCommentCnt(@Param("id") Long id, @Param("count") int count); // 댓글수 감소는 대댓글을 포함하므로 갯수를 지정

    Board findById(Long id);

    List<BoardCommentDTO> findByIdWithComment(Long id);

    List<Board> findPagedBoard(Criteria criteria);
    List<Board> findPagedBoardWithMemberId(@Param("criteria") Criteria criteria, @Param("memberId") Long memberId);

    int save(Board board);

    int update(@Param("id") Long id, @Param("updateParam") Board updateParam);

    int syncWriter(@Param("memberId") Long memberId, @Param("updateName") String updateName);

    int update(long viewCount);

    int delete(Long id);
}
