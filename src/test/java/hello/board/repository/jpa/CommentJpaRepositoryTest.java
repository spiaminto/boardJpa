package hello.board.repository.jpa;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.enums.Category;
import hello.board.repository.query.CommentQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CommentJpaRepositoryTest {

    @Autowired
    CommentJpaRepository commentJpaRepository;

    @Autowired
    BoardJpaRepository boardJpaRepository;

    @Autowired
    CommentQueryRepository commentQueryRepository;

    @Autowired
    EntityManager em;
    @Test
    void countByBoardEqualsAndGroupIdEquals() {
        long count = commentJpaRepository.countByBoardIdEqualsAndGroupIdEquals(98L, 0L);
        log.info("count = {}", count); // expected 21 while filtered (22)
    }

    @Test
    void findByBoardId() {
        List<Comment> findComment = commentJpaRepository.findByBoardId(98L);
        printComment(findComment);
        log.info("size = {}", findComment.size()); // expected 22
    }

    @Test
    void findPagedCommentWithMemberId() {
        // countTotalCommentWithMemberId 와 같이 테스튼

        Criteria criteria = new Criteria(); // currentPage = 1, contentPerPage = 12
        List<Comment> pagedComment = commentQueryRepository.findPagedCommentWithMemberId(criteria, 26L);
        printComment(pagedComment);

        criteria.setCurrentPage(2);
        List<Comment> pagedComment2 = commentQueryRepository.findPagedCommentWithMemberId(criteria, 26L);
        printComment(pagedComment2);

        criteria.setCurrentPage(1);
        criteria.setCategory(Category.FREE);
        criteria.setOption("content");
        criteria.setKeyword("eee");
        List<Comment> pagedFilteredComment = commentQueryRepository.findPagedCommentWithMemberId(criteria, 26L);
        printComment(pagedFilteredComment);

        criteria.setCurrentPage(2);
        List<Comment> pagedFilteredComment2  = commentQueryRepository.findPagedCommentWithMemberId(criteria, 26L);
        printComment(pagedFilteredComment2);

    }

    @Test
    @Transactional
    void updateWriterAndTargetTest() {
        // memberId = 42L = "spiamintoGG", count 도 같이 검증
        List<Comment> byMemberId = commentJpaRepository.findByMemberId(42L);
        long writerCount = commentJpaRepository.countByMemberIdEquals(42L);
        printComment(byMemberId);
        log.info("=======================================");
        List<Comment> byTargetId = commentJpaRepository.findByTargetId(42L);
        long targetCount = commentJpaRepository.countByTargetIdEquals(42L);
        printComment(byTargetId);
        log.info("=======================================");

        int updatedWriterCount = commentJpaRepository.updateCommentWriterByMemberId(42L, "updatedUsername");
        int updatedTargetCount = commentJpaRepository.updateTargetByMemberId(42L, "updatedTarget");

        log.info("writerCount = {}, updatedWriterCount = {}", writerCount, updatedWriterCount);
        log.info("targetCoubt = {}, udpatedTargetCount = {}", targetCount, updatedTargetCount);

        em.flush();
        em.clear();

        byMemberId = commentJpaRepository.findByMemberId(42L);
        byTargetId = commentJpaRepository.findByTargetId(42L);

        byMemberId.forEach(comment -> assertThat(comment.getWriter()).isEqualTo("updatedUsername"));
        byTargetId.forEach(comment -> assertThat(comment.getTarget()).isEqualTo("updatedTarget"));


    }

    private static void printComment(List<Comment> findComment) {
        for (Comment comment :
                findComment) {
            log.info("comment = {}", comment);
        }
    }

}