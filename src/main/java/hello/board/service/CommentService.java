package hello.board.service;

import hello.board.domain.comment.Comment;
import hello.board.repository.BoardRepository;
import hello.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    /**
     * comment 받아서 save 하고 모댓글이면 groupId 설정 후 comment 반환
     * @param comment
     * @return 저장한 comment
     */
    @Transactional
    public Comment saveComment(Comment comment) {
        Comment savedComment = commentRepository.save(comment);

        // 모댓글이면, groupId = commentId 설정
        if (savedComment.getGroupDepth() == 0) {
            savedComment.setGroupId(comment.getCommentId());
            commentRepository.setGroupId(comment.getCommentId(), comment.getGroupId());
        }

        // 댓글 수 증가
        boardRepository.addCommentCnt(comment.getBoardId());

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
     * comment 의 대댓글 먼저 삭제하고, 댓글 삭제 후 삭제한 댓글 갯수 반환
     * @param id commentId
     * @return 삭제한 댓글 갯수
     */
    @Transactional
    public boolean deleteComment(Long id) {
        Comment findComment = commentRepository.findByCommentId(id);
        int originCommentCount = 0;
        int deletedReplyCount = 0;

        if (findComment.getGroupDepth() == 0) {
            // 모댓글 이면 답글 포함 카운트
            originCommentCount = commentRepository.countTotalCommentWithBoardIdAndGroupId(findComment.getBoardId(), findComment.getCommentId());
            deletedReplyCount = commentRepository.deleteReply(id); // 답글삭제
        } else {
            // 대댓글 이면 카운트 = 1
            originCommentCount = 1;
        }

        int deletedCommentCount = commentRepository.delete(id); // 댓글 삭제
        boardRepository.subtractCommentCnt(findComment.getBoardId(), deletedCommentCount + deletedReplyCount); // 댓글 수 감소

//        log.info("deleteComment() groupDepth = {}, origin={}, deletedComment={}, deletedReply={}", findComment.getGroupDepth(), originCommentCount, deletedCommentCount, deletedReplyCount);
        return originCommentCount == deletedCommentCount + deletedReplyCount;
    }

}
