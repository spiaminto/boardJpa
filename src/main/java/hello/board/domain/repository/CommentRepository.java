package hello.board.domain.repository;

import hello.board.domain.comment.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Optional<Comment> findByCommentId(Long commentId);

    List<Comment> findByBoardId(Long boardId);

    Comment save(Comment comment);

    int update(Long commentId, Comment updateParam);

    int delete(Long commentId);

    // db 에서 foreign key cascade? 사용해서 지워야 할듯. 일단 따로 delete 하도록함
    int deleteByBoardId(Long boardId);
}
