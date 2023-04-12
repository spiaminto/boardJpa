package hello.board.repository;

import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.repository.mybatis.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentRepository {

    private final CommentMapper commentMapper;

    public Integer countTotalCommentWithMemberId(Criteria criteria, Long memberId) {
        return commentMapper.countTotalCommentWithMemberId(criteria, memberId);
    }

    public Integer countTotalTargetWithMemberId(Long memberId) {
        return commentMapper.countTotalTargetWithMemberId(memberId);
    }

    public Integer countTotalCommentWithBoardIdAndGroupId(Long boardId, Long groupId) {
        return commentMapper.countTotalCommentWithBoardIdAndGroupId(boardId, groupId);
    }

    public Comment findByCommentId(Long commentId) {
        return commentMapper.findByCommentId(commentId);
    }

    public List<Comment> findByBoardId(Long boardId) {
        return commentMapper.findByBoardId(boardId);
    }

    public List<Comment> findPagedCommentWithMemberId(Criteria criteria, Long memberId) {
        return commentMapper.findPagedCommentWithMemberId(criteria, memberId);
    }

    public Comment save(Comment comment) {
        commentMapper.save(comment);
        return comment;
    }

    public int update(Long commentId, Comment updateParam) {
        int rowNum = commentMapper.update(commentId, updateParam);
//        log.info("update() row = {}", rowNum);
        return rowNum;
    }

    public int syncWriter(Long memberId, String updateName) {
        return commentMapper.syncWriter(memberId, updateName);
    }


    public int syncTarget(Long memberId, String updateName) {
        return commentMapper.syncTarget(memberId, updateName);
    }

    public int deleteReplyByTargetId(Long targetId) {
        return commentMapper.deleteReplyByTargetId(targetId);
    }

    public int delete(Long commentId) {
        int rowNum = commentMapper.delete(commentId);
//        log.info("delete() row = {}", rowNum);
        return rowNum;
    }

    public int deleteReply(Long id) {
        int rowNum = commentMapper.deleteReply(id);
//        log.info("deleteReply() row = {}", rowNum);
        return rowNum;
    }

    public void setGroupId(Long id, Long groupId) {
        commentMapper.setGroupId(id, groupId);
    }
}
