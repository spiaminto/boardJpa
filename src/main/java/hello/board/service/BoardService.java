package hello.board.service;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.repository.BoardCommentDTO;
import hello.board.repository.BoardCommentDTOMapper;
import hello.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardCommentDTOMapper boardCommentDTOMapper = BoardCommentDTOMapper.INSTANCE;

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
        Map result = new HashMap();

        List<BoardCommentDTO> list = boardRepository.findByIdWithComment(id);
        if (list.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "board not found"); // board 가 없는 경우

        // BoardCommentDTO -> Board, Comment 맵핑
        Board findBoard = boardCommentDTOMapper.toBoard(list.stream().findFirst().get());
        List<Comment> findCommentList = list.stream()
                .filter(item -> item.getCommentId() != null)    // 댓글이 없는경우 제외
                .map(c -> boardCommentDTOMapper.toComment(c)).collect(Collectors.toList());

        result.put("board", findBoard);
        result.put("commentList", findCommentList);

        boardRepository.updateViewCount(id);
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
