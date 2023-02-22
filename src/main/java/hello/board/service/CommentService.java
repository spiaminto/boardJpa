package hello.board.service;

import hello.board.domain.comment.Comment;
import hello.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * comment 받아서 save 하고 모댓글이면 groupId 설정 후 comment 반환
     * @param comment
     * @return 저장한 comment
     */
    public Comment saveComment(Comment comment) {
        Comment savedComment = commentRepository.save(comment);

        // 모댓글이면, groupId = commentId 설정
        if (savedComment.getGroupDepth() == 0) {
            savedComment.setGroupId(comment.getCommentId());
            commentRepository.setGroupId(comment.getCommentId(), comment.getGroupId());
        }

        return savedComment;
    }

    /**
     * comment update 하고 update 한 comment findById 해서 반환
     * @param id
     * @param updateParam
     * @return db에서 수정한 열이 1개면 comment 반환, 아니면 null
     */
    public Comment updateComment(Long id, Comment updateParam) {
        int result = commentRepository.update(id, updateParam);
        return result == 1 ? commentRepository.findByCommentId(id) : null;
    }

    /**
     * comment 의 대댓글 먼저 삭제하고, 댓글 삭제 후 삭제한 열 갯수 반환
     * @param id
     * @return 삭제한 열 갯수
     */
    public int deleteComment(Long id) {
        commentRepository.deleteReply(id);
        return commentRepository.delete(id);
    }

}
