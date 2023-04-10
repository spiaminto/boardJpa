package hello.board.repository;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.repository.mybatis.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BoardRepository {

    private final BoardMapper boardMapper;

    public Integer countTotalBoard(Criteria criteria) {
        return boardMapper.countTotalBoard(criteria);
    }

    public Integer countTotalBoardWithMemberId(Criteria criteria, Long memberId) {
        return boardMapper.countTotalBoardWithMemberId(criteria, memberId);
    }

    public void updateViewCount(long id) {
        boardMapper.updateViewCount(id);
    }


    public Board findById(Long id) {
        return boardMapper.findById(id);
    }

    // 검색 + 페이징
    public List<Board> findPagedBoard(Criteria criteria) {
        return boardMapper.findPagedBoard(criteria);
    }

    public List<Board> findPagedBoardWithMemberId(Criteria criteria, Long memberId) {
        return boardMapper.findPagedBoardWithMemberId(criteria, memberId);
    }


    public Board save(Board board) {
        boardMapper.save(board);
        return findById(board.getId());
    }


    public Board update(Long id, Board updateParam) {
        int row = boardMapper.update(id, updateParam);
        if (row != 1) return null;
        return findById(id);
    }

    // 필요시 반환
    public int delete(Long id) throws RuntimeException {
        return boardMapper.delete(id);
    }

    public int syncWriter(Long memberId, String updateName) {
        return boardMapper.syncWriter(memberId, updateName);
    }

}
