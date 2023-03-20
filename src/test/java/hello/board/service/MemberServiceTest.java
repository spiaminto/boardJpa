package hello.board.service;

import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
//트랜잭션을 강제로 Exception 일으켜 롤백하므로, 테스트에서 직접 롤백하지 않음
//@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    void syncUserName() {

        // CommentRepository 에 syncTarget() 의 //Test 주석을 풀고 테스트.
//        memberService.syncUserName(1L, "test", "test");

        // 처리후 재조회 (롤백여부 확인)
        memberService.myPage(new Criteria(), 1L);
    }

    @Test
    void deleteMember() {
        memberService.deleteMember(1L);

        // 재조회
        Member findMember = memberService.findById(1L);
        log.info("member = {}", findMember);
    }
}