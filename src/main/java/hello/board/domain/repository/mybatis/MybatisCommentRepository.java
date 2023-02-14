package hello.board.domain.repository.mybatis;

import hello.board.domain.comment.Comment;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.repository.CommentRepository;
import hello.board.domain.repository.ResultDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisCommentRepository implements CommentRepository {

    private final CommentMapper commentMapper;

    @Override
    public Integer countTotalCommentWithMemberId(Criteria criteria, Long memberId) {
        return commentMapper.countTotalCommentWithMemberId(criteria, memberId);
    }

    @Override
    public Comment findByCommentId(Long commentId) {
        return commentMapper.findByCommentId(commentId);
    }

    @Override
    public List<Comment> findByBoardId(Long boardId) {
        return commentMapper.findByBoardId(boardId);
    }

    @Override
    public List<Comment> findPagedCommentWithMemberId(Criteria criteria, Long memberId) {
        return commentMapper.findPagedCommentWithMemberId(criteria, memberId);
    }

    // DB 에 저장되는 commentID 를 전달하기 위해 Comment 리턴 (마이바티스에서 id 자동세팅)
    @Override
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

    @Override
    public int update(Long commentId, Comment updateParam) {
        updateParam.setUpdateDate(LocalDateTime.now());
        return commentMapper.update(commentId, updateParam);
    }

    @Override
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

    @Override
    public int delete(Long commentId) {
        // 답글 먼저삭제 (cascade)
        commentMapper.deleteReply(commentId);

        return commentMapper.delete(commentId);
    }

    @Override
    public int deleteByBoardId(Long boardId) {
        return commentMapper.deleteByBoardId(boardId);
    }

}
