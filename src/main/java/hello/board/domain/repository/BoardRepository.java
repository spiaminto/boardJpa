package hello.board.domain.repository;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;

import java.util.List;

public interface BoardRepository {

    Integer countTotalBoard(Criteria criteria);

    Integer countTotalBoardWithMemberId(Criteria criteria, Long memberId);

    void updateViewCount(long id);

    // 일단은 글이 없는 경우는 없다고 가정.
    Board findById(Long id);

    // 내가쓴 댓글 로드할때 같이 로드할 board
    List<Board> findByIdList(List<Long> idList);
    
    // 페이징 된 보드
    List<Board> findPagedBoard(Criteria criteria);

    List<Board> findPagedBoardWithMemberId(Criteria criteria, Long memberId);

    List<Board> findPagedAndCategorizedBoard(Criteria criteria);

    Board save(Board board);

    Board update(Long id, Board updateParam);

    // 필요시 boolean 으로 수정
    int delete(Long id);

}
