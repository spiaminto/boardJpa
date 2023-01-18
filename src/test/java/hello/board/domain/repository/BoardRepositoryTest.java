package hello.board.domain.repository;

import hello.board.domain.repository.mybatis.BoardMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// 스프링 컨테이너를 이용해 트랜잭션
@SpringBootTest
@Transactional
class BoardRepositoryTest {


}