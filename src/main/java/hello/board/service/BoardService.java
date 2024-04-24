package hello.board.service;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import hello.board.repository.jpa.BoardJpaRepository;
import hello.board.repository.query.BoardQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardJpaRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final ImageService imageService;

    public long countTotalBoard(Criteria criteria) {
        return boardQueryRepository.countTotalBoard(criteria);
    }

    public Board findById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글 입니다."));
    }

    public List<Board> findPagedBoard(Criteria criteria) {
        return boardQueryRepository.findPagedBoard(criteria);
    }

    /**
     * board.id 를 받아 내용과 commentList 를 담은 Map 반환
     * @param id
     * @return Map<String, Object> board, commentList
     */
    @Transactional
    public Map<String, Object> readBoard(Long id) {
        Map result = new HashMap();

        Board boardWithComment = boardQueryRepository.findByIdWithComment(id);
        result.put("board", boardWithComment);
        result.put("commentList", boardWithComment.getCommentList());
        if (boardWithComment == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "board not found"); // board 가 없는 경우
        boardRepository.updateViewCount(id);
        return result;
    }

    @Transactional
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    @Transactional
    public Board updateBoard(Long id, Board updateParam) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
        board.updateBoard(updateParam);

        return board; // 변경감지 후 수정된 엔티티 반환
    }

    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
        // 글 삭제
        boardRepository.delete(board);
        // 이미지삭제
        boolean isSuccess = imageService.deleteImageByBoardId(id);
        log.info("이미지 삭제 isSuccess = {}", isSuccess);
        // 댓글 삭제 comment delete onCascade (mysql)
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
        return findBoard.getMember().getId().equals(currentMember.getId());
    }

}
