package hello.board.domain.repository;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.repository.ResultDTO;
import hello.board.domain.repository.mybatis.BoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class BoardRepository {

    private final BoardMapper boardMapper;

    public BoardRepository(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }


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


    public List<Board> findByIdList(List<Long> idList) {
        List<Board> boardList = new ArrayList<>();
        for (Long boardId :
                idList) {
            boardList.add(boardMapper.findById(boardId));
        }
        return boardList;
    }

    // 검색 + 페이징

    public List<Board> findPagedBoard(Criteria criteria) {
        return boardMapper.findPagedBoard(criteria);
    }


    public List<Board> findPagedBoardWithMemberId(Criteria criteria, Long memberId) {
        return boardMapper.findPagedBoardWithMemberId(criteria, memberId);
    }


    public List<Board> findPagedAndCategorizedBoard(Criteria criteria) {
        return boardMapper.findPagedAndCategorizedBoard(criteria);
    }


    public Board save(Board board) {
        board.setRegedate(LocalDateTime.now());
        board.setUpdateDate(LocalDateTime.now());
        int row = boardMapper.save(board);
        if (row != 1) return null;
        return board;
    }


    public Board update(Long id, Board updateParam) {
        updateParam.setUpdateDate(LocalDateTime.now());
        int row = boardMapper.update(id, updateParam);
        if (row != 1) return null;
        return findById(id);
    }


    public ResultDTO syncWriter(Long memberId, String updateName) {
        try {
            boardMapper.syncWriter(memberId, updateName);
            return new ResultDTO(true);
        } catch (Exception e) {
            return new ResultDTO(false, e.toString(), e.getMessage(), "BoardMapper.syncWriter 오류");
        }
    }

    // 필요시 반환
    public int delete(Long id) throws RuntimeException {
        return boardMapper.delete(id);
    }
}
