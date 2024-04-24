package hello.board.service;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.domain.enums.Category;
import hello.board.repository.legacy.BoardLegacyRepository;
import hello.board.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Slf4j
class CommentServiceTest {

    @Autowired
    CommentService commentService;
    @Autowired
    BoardLegacyRepository boardLegacyRepository;
    @Autowired
    CommentRepository commentRepository;

    Comment testComment;
    Board testBoard;

    /**
     * 테스트용
     * Board : id = 1, writer = 'testname', member_id = 1,
     * Member : id = 1, username = testname'
     */

    @BeforeEach
    public void setup() {
        testBoard = Board.builder().id(1L).build();
        testComment = Comment.builder()
                .writer("testname").content("test")
                .regDate(LocalDateTime.now())
                .board(testBoard).memberId(1L)
                .category(Category.FREE)
                .groupId(0L).groupOrder(0).groupDepth(0).build();
    }

    @Test
    @Transactional
    void saveComment() {
        commentService.saveComment(testComment);
        log.info("comment_cnt = {}", boardLegacyRepository.findById(testComment.getBoard().getId()).getCommentCnt());
    }

    @Test
    @Transactional
    void deleteComment() {
        Comment comment = commentService.saveComment(testComment);

        Comment testReply = Comment.builder()
                .writer("testname").content("test")
                .regDate(LocalDateTime.now())
                .board(testBoard).memberId(1L)
                .category(Category.FREE)
                .groupId(comment.getId()).groupOrder(0).groupDepth(1).build();

        Comment reply = commentService.saveComment(testComment);

        try {
            commentService.deleteComment(testReply.getId());
        } catch(IllegalStateException e) {
            // 롤백 확인
            log.info("comment = {}", commentRepository.findByCommentId(comment.getId()));
            log.info("reply = {}", commentRepository.findByCommentId(reply.getId()));
        } finally {
            log.info("comment_cnt = {}", boardLegacyRepository.findById(testComment.getBoard().getId()).getCommentCnt());
        }

    }

    // 롤백 안됬을때 사용.
    @Test
    void clear() {
        List<Comment> commentList = commentRepository.findByBoardId(1L);
        commentList.forEach(comment -> commentService.deleteComment(comment.getId()));
    }
}