package hello.board.domain.repository;

import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.repository.ResultDTO;
import hello.board.domain.repository.mybatis.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final CommentMapper commentMapper;

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
        comment.setRegDate(LocalDateTime.now());
        comment.setUpdateDate(LocalDateTime.now());
        commentMapper.save(comment);

        // 모댓글 groupId 설정
        if (comment.getGroupDepth() == 0) {
            comment.setGroupId(comment.getCommentId());
            commentMapper.setGroupId(comment.getCommentId(), comment.getGroupId());
        }
        return comment;
    }

    public int update(Long commentId, Comment updateParam) {
        updateParam.setUpdateDate(LocalDateTime.now());
        return commentMapper.update(commentId, updateParam);
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
        // 답글 먼저삭제 (cascade)
        commentMapper.deleteReply(commentId);

        return commentMapper.delete(commentId);
    }

    public int deleteByBoardId(Long boardId) {
        return commentMapper.deleteByBoardId(boardId);
    }

}
