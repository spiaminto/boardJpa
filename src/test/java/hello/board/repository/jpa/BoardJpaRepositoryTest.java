package hello.board.repository.jpa;

import hello.board.domain.board.Board;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class BoardJpaRepositoryTest {

    @Autowired
    BoardJpaRepository boardJpaRepository;
    @Autowired
    EntityManager em;

    @Test
    @Transactional
    void findByMemberId() {
        List<Board> boards = boardJpaRepository.findByMemberId(1L);
        boards.forEach(board -> log.info(board.toString()));
    }

    @Test
    @Transactional
    void updateBoardByMemberId() {

        List<Board> boards = boardJpaRepository.findByMemberId(1L);
        boards.forEach(board -> log.info(board.toString()));
        boards.forEach(board -> assertThat(board.getWriter()).isEqualTo("testname"));

        // memberId = 1 writer(username) = testname
        int affectedRows = boardJpaRepository.updateBoardWriterByMemberId(1L, "updatedName");
        log.info("== updated == affectedRows = {}", affectedRows);
        /*
        List<Board> updatedBoards = boardJpaRepository.findByMemberId(1L); // 영속성 컨텍스트가 DB 보다 우선
        updatedBoards.forEach(board -> log.info(board.toString())); // 따라서 수정되지 않은 결과 출력
        updatedBoards.forEach(board -> assertThat(board.getWriter()).isEqualTo("updatedName")); // 에러
         */

        // 영속성 컨텍스트 갱신
        em.flush();
        em.clear();

        List<Board> updatedBoardsWithClear = boardJpaRepository.findByMemberId(1L);
        updatedBoardsWithClear.forEach(board -> log.info(board.toString()));
        updatedBoardsWithClear.forEach(board -> assertThat(board.getWriter()).isEqualTo("updatedName"));
    }

}