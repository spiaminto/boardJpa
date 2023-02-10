package hello.board.domain.repository.mybatis;

import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {

    Comment findByCommentId(Long commentId);

    List<Comment> findByBoardId(Long boardId);

    List<Comment> findPagedCommentWithMemberId(@Param("criteria") Criteria criteria, @Param("memberId") Long memberId);

    Integer countTotalCommentWithMemberId(@Param("criteria")Criteria criteria, @Param("memberId")Long memberId);

    int save(Comment comment);

    int setGroupId(@Param("commentId") Long commentId, @Param("groupId") Long groupId);

    int update(@Param("commentId") Long commentId, @Param("updateParam") Comment updateParam);

    int delete(Long commentId);

    int deleteReply(Long commentId);

    int deleteByBoardId(Long boardId);

}
