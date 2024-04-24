package hello.board.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.enums.Category;
import hello.board.repository.jpa.BoardJpaRepository;
import hello.board.repository.query.BoardQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Slf4j
class BoardQueryRepositoryTest {

    @Autowired
    BoardQueryRepository boardQueryRepository;

    @Autowired
    BoardJpaRepository boardJpaRepository;

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Test
    public void countTotalBoard() {
        List<Board> all = boardJpaRepository.findAll();
        log.info("count = {}", all.size());

        Criteria criteria = new Criteria(0, 0, null, null);
        Long count = boardQueryRepository.countTotalBoard(criteria);
        log.info("count = {}", count);

        Criteria filterCriteria = new Criteria(0, 10, "all", Category.ALL, "title", "까마귀");
        Long filteredCount = boardQueryRepository.countTotalBoard(filterCriteria);
        log.info("filtered count = {}", filteredCount);
    }

    @Test
    public void countTotalBoardWithMemberId() {
        Criteria filterCriteria = new Criteria(0, 10, "all", Category.ALL, "title", "까마귀");
        Long result;

        result = boardQueryRepository.countTotalBoardWithMemberId(filterCriteria, 22L);
        log.info("expected 1 result = {}", result);

        result = boardQueryRepository.countTotalBoardWithMemberId(filterCriteria, 1L);
        log.info("expected 0 result = {}", result);

        result = boardQueryRepository.countTotalBoardWithMemberId(new Criteria(), 42L);
        log.info("expected 103 result = {}", result);
    }

    @Test
    @Transactional // 영속성 컨텍스트 종료방지
    public void findByIdWithComment() {
        Board findBoard = boardQueryRepository.findByIdWithComment(98L);

        // didn't fetched
        log.info("commentList size = {}", findBoard.getCommentList().size());

        log.info("==================================");

        // fetch
        log.info("board = {}", findBoard);
    }

    @Test
    @Transactional
    // findPagedBoard 쿼리 최적화 전 테스트
    public void findPagedBoardLegacy() {
        Criteria criteria = new Criteria(); // currentPage = 1, contentPerPage = 12
        List<Board> pagedBoard = boardQueryRepository.findPagedBoard(criteria);
        printBoard(pagedBoard);

        criteria.setCurrentPage(2);
        List<Board> pagedBoard2 = boardQueryRepository.findPagedBoard(criteria);
        printBoard(pagedBoard2);
    }

    @Test
    @Transactional
    // findPagedBoard 쿼리 최적화 후 테스트
    public void findPagedBoardBatchSize() {
        Criteria criteria = new Criteria(); // currentPage = 1, contentPerPage = 12
        List<Board> pagedBoard = boardQueryRepository.findPagedBoard(criteria);
        printBoard(pagedBoard);

        criteria.setCurrentPage(2);
        List<Board> pagedBoard2 = boardQueryRepository.findPagedBoard(criteria);
        printBoard(pagedBoard2);

        criteria.setCurrentPage(1);
        criteria.setCategory(Category.FREE);
        criteria.setOption("title");
        criteria.setKeyword("페이징");
        List<Board> pagedFilteredBoard = boardQueryRepository.findPagedBoard(criteria);
        printBoard(pagedFilteredBoard);

        criteria.setCurrentPage(2);
        List<Board> pagedFilterdBoard2 = boardQueryRepository.findPagedBoard(criteria);
        printBoard(pagedFilterdBoard2);
    }

    private static void printBoard(List<Board> pagedBoard) {
        for (Board board :
                pagedBoard) {
            log.info("board = {}", board);
        }
        log.info("size = {}", pagedBoard.size());
    }


}