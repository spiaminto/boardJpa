package hello.board.repository;

import hello.board.domain.board.Board;
import hello.board.domain.enums.Category;
import hello.board.repository.mybatis.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// 스프링 컨테이너를 이용해 트랜잭션
@SpringBootTest
@Transactional
@RequiredArgsConstructor
@Slf4j

class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Test
    public void saveTest() {
        Board board = new Board("String title", "String writer",0L,
                "String content", Category.ALL, LocalDateTime.now());
        try {
            boardRepository.save(board);
        } catch (DataAccessException e) {
            log.info("e = {}", e.getMessage());
            log.info("e = {}", e.getCause());
            log.info("e = {}", e.toString());
            log.info("e = {}", e.getRootCause());
            e.printStackTrace();
        }


    }


}