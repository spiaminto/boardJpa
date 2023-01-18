package hello.board.domain.repository.mybatis;

import hello.board.domain.comment.Comment;
import hello.board.domain.repository.CommentRepository;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MybatisCommentRepository implements CommentRepository {

    private final CommentMapper commentMapper;

    // 생성자 주입
    public MybatisCommentRepository(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public Optional<Comment> findByCommentId(Long commentId) {
        return commentMapper.findByCommentId(commentId);
    }

    @Override
    public List<Comment> findByBoardId(Long boardId) {
        return commentMapper.findByBoardId(boardId);
    }

    // DB 에 저장되는 commentID 를 전달하기 위해 Comment 리턴 (마이바티스에서 id 자동세팅)
    @Override
    public Comment save(Comment comment) {
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
        return commentMapper.update(commentId, updateParam);
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
