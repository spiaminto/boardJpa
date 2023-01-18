package hello.board.domain.repository;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;

import java.util.List;

public interface BoardRepository {

    Integer countTotalBoard(Criteria criteria);

    void updateViewCount(long id);

    // 일단은 글이 없는 경우는 없다고 가정.
    Board findById(Long id);

    List<Board> findAll(BoardSerachCond cond);
    
    // 페이징 된 보드
    List<Board> findPagedBoard(Criteria criteria);

    List<Board> findByWriter(String writer);

    Board save(Board board);

    Board update(Long id, Board updateParam);

    // 필요시 boolean 으로 수정
    void delete(Long id);

}
