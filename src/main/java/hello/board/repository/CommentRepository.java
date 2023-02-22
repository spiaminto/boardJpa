package hello.board.repository;

import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.repository.mybatis.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentRepository {

    private final CommentMapper commentMapper;
    int rowNum = 0;

    public Integer countTotalCommentWithMemberId(Criteria criteria, Long memberId) {
        return commentMapper.countTotalCommentWithMemberId(criteria, memberId);
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

    // DB 에 저장되는 commentID 를 전달하기 위해 Comment 리턴 (마이바티스에서 id 자동세팅)
    public Comment save(Comment comment) {
        rowNum = commentMapper.save(comment);
        log.info("save() row = {}", rowNum);

        return comment;
    }

    public int update(Long commentId, Comment updateParam) {
        rowNum = commentMapper.update(commentId, updateParam);
        log.info("update() row = {}", rowNum);
        return rowNum;
    }

    public ResultDTO syncWriterAndTarget(Long memberId, String updateName) {
        boolean flag = false;
        try {
            commentMapper.syncWriter(memberId, updateName);
            flag = true;
            commentMapper.syncTarget(memberId, updateName);
            return new ResultDTO(true);
        } catch (Exception e) {
            if (flag) {
                return new ResultDTO(false, e.toString(), e.getMessage(), "commentMapper.syncTarget 오류");
            } else {
                return new ResultDTO(false, e.toString(), e.getMessage(), "commentMapper.syncWriter 오류");
            }

        }
    }

    public int deleteReplyByTargetId(Long targetId) {
        return commentMapper.deleteReplyByTargetId(targetId);
    }

    public int delete(Long commentId) {
        rowNum = commentMapper.delete(commentId);
        log.info("delete() row = {}", rowNum);
        return rowNum;
    }

    public int deleteReply(Long id) {
        rowNum = commentMapper.deleteReply(id);
        log.info("deleteReply() row = {}", rowNum);
        return rowNum;
    }

    public void setGroupId(Long id, Long groupId) {
        commentMapper.setGroupId(id, groupId);
    }
}
