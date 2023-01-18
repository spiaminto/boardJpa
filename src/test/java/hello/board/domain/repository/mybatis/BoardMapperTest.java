package hello.board.domain.repository.mybatis;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.repository.BoardSerachCond;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// save 를 모든 테스트에서 사용

// 스프링 컨테이너를 이용해 트랜잭션
@SpringBootTest
@Transactional
@Slf4j
class BoardMapperTest {

    @Autowired
    BoardMapper boardMapper;

    Board board = new Board("testTitle_",
            "test",
            "testContent",
            LocalDateTime.now()
    );
    
    // id = 10 으로 테스트

    @Test
    public void countTotalBoard() {
        Criteria criteria = new Criteria();
        criteria.setKeyword("까마귀");
        Integer count = boardMapper.countTotalBoard(criteria);
        log.info("totalCount = {}", count);
    }

    @Test
    public void findPagedBoard() {
        // current 1, perpage 10
        Criteria criteria = new Criteria();

        // 기댓값: 7,8,9번째 행 / startRowNum: 6(7~9)
//        Criteria criteria2 = new Criteria(3, 3);

//        Criteria criteria2 = new Criteria(1,10,"", "까마귀");
        Criteria criteria2 = new Criteria(1,7,"content", "test");

        List<Board> pagedBoard = boardMapper.findPagedBoard(criteria2);
        for (Board board :
                pagedBoard) {
            log.info("BOARD = {}", board.toString());
        }
    }

    @Test
    public void findById() {
        boardMapper.save(board);
        Board findBoard = boardMapper.findById(board.getId());
        Assertions.assertThat(findBoard).isEqualTo(board);
    }

    @Test
    public void findAll() {
        List<Board> boardList = boardMapper.findAll(new BoardSerachCond("까마귀"));
        for (Board board:
             boardList) {
            log.info("@found = {}", board.toString());
        }
    }

    @Test
    public void findByWriter() {
        List<Board> boardList = boardMapper.findByWriter("jkljilohio");
        for (Board board:
                boardList) {
            log.info("@found = {}", board.toString());
        }
    }

    @Test
    public void testSave() {

        int row = boardMapper.save(board);

        log.info("effected row = {}", row);
        log.info("board.id = {}", board.getId());

    }

    // 저장용, 테스트X, @Transactional 해제
    @Test
    public void testSave2() {
        for(int i = 56; i < 87; i++) {
            board.setTitle("testTitle_" + i);
            board.setContent("testContent_" + i);
            boardMapper.save(board);
        }

    }

    @Test
    public void testUpdate() {
        
        Board updateParam = new Board("updateTitle",
                "update",
                "updateContent"
                );
        updateParam.setRegedate(LocalDateTime.now());

        int row = boardMapper.update(10L, updateParam);
        log.info("effected row = {}", row);
    }

    @Test
    public void testDelete() {

        int row = boardMapper.delete(10L);
        log.info("effected row = {}", row);
    }

    @Test
    public void tttt() {
        String query = "/board/edit/96?currentPage=1.option=title.keyword=원";
        query = query.replace(".", "&");
        log.info(query);
    }
}