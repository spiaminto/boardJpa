package hello.board.service;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.repository.BoardRepository;
import hello.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public int countTotalBoard(Criteria criteria) {
        return boardRepository.countTotalBoard(criteria);
    }

    public Board findById(Long id) {
        return boardRepository.findById(id);
    }

    public List<Board> findPagedBoard(Criteria criteria) {
        return boardRepository.findPagedBoard(criteria);
    }

    /**
     * board.id 를 받아 내용과 commentList 를 담은 Map 반환
     * @param id
     * @return Map<String, Object> board, commentList
     */
    public Map<String, Object> readBoard(Long id) {
        boardRepository.updateViewCount(id);
        Map result = new HashMap();
        result.put("board", boardRepository.findById(id));
        result.put("commentList", commentRepository.findByBoardId(id));
        return result;
    }

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    public Board updateBoard(Long id, Board updateParam) {
        return boardRepository.update(id, updateParam);
    }

    public int deleteBoard(Long id) {
        return boardRepository.delete(id); // comment delete onCascade
    }

    /**
     * 멤버의 id 와 board.id 가 같은지 또는 admin 인지 확인
     * @param currentMember
     * @param findBoard
     * @return id가 같거나, admin 일때 true
     */
    public boolean isSameWriter(Member currentMember, Board findBoard) {

        if (currentMember.getUsername().equals("admin")) {
            //관리자
            return true;
        }

        return findBoard.getMemberId().equals(currentMember.getId());
    }



}
