package hello.board.repository.mybatis;

import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.enums.Category;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;

@SpringBootTest
@Slf4j
@Transactional
/**
 * 이 테스트가 제일 나은듯? -> 페이지 검색부터 다시 막씀
 */
class CommentMapperTest {

    @Autowired
    CommentMapper commentMapper;
    Comment testComment = new Comment(98L, "asdf", "댓글");
    Comment testComment2 = new Comment(98L, "asdfasdf", "댓글2");


    @Test
    void findByBoardId() {
        commentMapper.save(testComment);
        commentMapper.save(testComment2);

        List<Comment> savedCommentList = new ArrayList<Comment>();
        savedCommentList.add(testComment);
        savedCommentList.add(testComment2);

        List<Comment> findCommentList = commentMapper.findByBoardId(testComment.getBoardId());
        //                          from Index, To Index(size 넣으면 last Index)
        List<Comment> lastTwoCommentList = findCommentList.subList(findCommentList.size() - 2, findCommentList.size());

        log.info("size = {}", lastTwoCommentList.size());
        findCommentList.forEach(comment -> log.info("comment = {}", comment));

        // 기존 DB 에 있던 comment 는 test 결과에 영향주면 X 이므로, 끝에 2개 잘라서 확인
        Assertions.assertThat(lastTwoCommentList).isEqualTo(savedCommentList);

    }

    @Test
    void findByCommentId() {
        // groupId set 이 repository 에 있는데 mapper 로 테스트 하는건 아닌듯.
        Comment testComment3 = new Comment(1L, 1L, "test",
                "String content", 289L, 5555, 0, Category.ALL);
        commentMapper.save(testComment3);
        Optional<Comment> first = commentMapper.findByBoardId(1L).stream().findFirst();
        if (first.isPresent()) {
            commentMapper.findByCommentId(first.get().getCommentId());
        }

    }

    @Test
    // 사실상 save + findByCommentId 동시 테스트
    void save() {
        // save 시 mybatis 에 의해 testComment.commentId 가 자동으로 DB 에 저장되는 commentId 값으로 설정됨
       commentMapper.save(testComment);

       // Optional<Comment> 로 찾아 .get() 하면 값 뽑아옴. (없으면 Exception)
       Comment findComment = commentMapper.findByCommentId(testComment.getCommentId());

       log.info("findComment = {}", findComment);
       log.info("testComment = {}", testComment.toString());

       // DB 에서 찾은 findComment 와 저장하려고 만든 testComment 가 같은지 확인
       Assertions.assertThat(findComment).isEqualTo(testComment);

       // 문제1. LocalDateTime.now()
        // LocalDateTime.now() 에서 시간이 소수점(밀리세컨드)까지 밀려나오는 현상 발생
        // DB 에 저장된 regDate 는 (아마도) 밀리세컨드에서 반올림하여 저장됨
        // testComment(java.LocalDateTime.now()) 와 findComment(MySQL.DateTime) 이 다르게 나옴.
        // 따라서 java.LocalDateTime.now().withNano(0) 을 통해 밀리세컨드 절삭.
        // 다만 실행 속도상 1초 이상 차이나면 에러가 다시 날걸로 예상됨.
    }

    @Test
    void update() {
        commentMapper.save(testComment);

        Comment updateParam = new Comment(98L, "asdf", "updated");

        commentMapper.update(testComment.getCommentId(), updateParam);

        Comment findComment = commentMapper.findByCommentId(testComment.getCommentId());

        log.info("testComment = {}", testComment.toString());
        log.info("findComment = {}", findComment);
        log.info("updateParam = {}", updateParam);

        // update 되는 값만 테스트
        Assertions.assertThat(findComment.getWriter()).isEqualTo(updateParam.getWriter());
        Assertions.assertThat(findComment.getContent()).isEqualTo(updateParam.getContent());
        Assertions.assertThat(findComment.getRegDate()).isEqualTo(updateParam.getRegDate());
    }

    @Test
    void delete() {
        commentMapper.save(testComment);
        commentMapper.delete(testComment.getCommentId());

//        Assertions.assertThat(commentMapper.findByCommentId(testComment.getCommentId())).isEmpty();
    }

    @Test
    void findPagedCommentWithMemberIdTest() {

//        Comment testComment3 = new Comment(1L, 1L, "test", "String content",
//                6666L, 5555, 0, Category.ALL);

        Criteria criteria = new Criteria();
        criteria.setCategory(Category.FREE);
        criteria.setOption("content");
        criteria.setKeyword("재밌");
        criteria.setContentPerPage(5);

//        for(int i = 0; i < 20; i++) {
//            commentMapper.save(testComment3);
//            if (i%2 == 0) {
//            }
//        }

        List<Comment> pagedCommentWithMemberId = commentMapper.findPagedCommentWithMemberId(criteria,1L);

        // setTarget() 까지 확인
        for (Comment comment :
                pagedCommentWithMemberId) {
            log.info(comment.toString());
        }

    }
}