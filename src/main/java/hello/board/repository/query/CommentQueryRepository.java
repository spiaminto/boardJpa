package hello.board.repository.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.comment.Comment;
import hello.board.domain.comment.QComment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.enums.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static hello.board.domain.board.QBoard.board;
import static hello.board.domain.comment.QComment.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public long countTotalCommentWithMemberId(Criteria criteria, Long memberId) {
        return queryFactory.select(comment.count())
                .from(comment)
                .where(
                        memberIdEq(memberId),
                        categoryEq(criteria.getCategory()),
                        keywordEq(criteria.getKeyword())
                ).fetchFirst();
    }
    
    // 댓글 삭제시 모댓글의 경우, 해당 board의 id 와 댓글 대댓글 groupId 를 카운트하여 모두삭제
    public long countTotalCommentWithBoardIdAndMEmberId(Long boardId, Long groupId) {
        return queryFactory.select(comment.count())
                .from(comment)
                .where(
                        boardIdEq(boardId),
                        groupIdEq(groupId)
                ).fetchFirst();
    }

    // mypage 에서 보여줄 comment
    public List<Comment> findPagedCommentWithMemberId(Criteria criteria, Long memberId) {
        return queryFactory.selectFrom(comment)
                .where(
                        memberIdEq(memberId),
                        categoryEq(criteria.getCategory()),
                        keywordEq(criteria.getKeyword())
                )
                .offset(criteria.getStartRowNum())
                .limit(criteria.getContentPerPage())
                .fetch();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return comment.memberId.eq(memberId);
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null && category != Category.ALL ? comment.category.eq(category) : null;
    }

    private BooleanExpression keywordEq(String keyword) {
        if (!StringUtils.hasText(keyword)) return null; // keyword 없음
        return comment.content.contains(keyword); // comment 는 title 없음
    }

    // 댓글 삭제시 사용할 조건들. 검증용이라 사용여부를 고민해봐야.
    private BooleanExpression boardIdEq(Long boardId) { return comment.board.id.eq(boardId); }
    private BooleanExpression groupIdEq(Long groupId) { return comment.groupId.eq(groupId); }

}
