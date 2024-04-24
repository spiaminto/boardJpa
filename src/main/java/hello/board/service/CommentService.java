package hello.board.service;

import hello.board.domain.comment.Comment;
import hello.board.repository.jpa.BoardJpaRepository;
import hello.board.repository.jpa.CommentJpaRepository;
import hello.board.repository.query.CommentQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentJpaRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final BoardJpaRepository boardRepository;

    /**
     * comment 받아서 save 하고 모댓글이면 groupId 설정 후 comment 반환
     * @param comment
     * @return 저장한 comment
     */
    public Comment saveComment(Comment comment) {

        // 댓글저장
        Comment savedComment = commentRepository.save(comment);

        // 모댓글이면, groupId = commentId 설정
        if (comment.getGroupDepth() == 0) {
            comment.setGroupId(comment.getId()); // 변경감지
        }

        // 댓글 수 증가
        boardRepository.addCommentCount(comment.getBoard().getId());

        return savedComment;
    }

    /**
     * comment update 하고 update 한 comment findById 해서 반환
     * @param id
     * @param updateParam
     * @return db에서 수정한 열이 1개면 comment 반환, 아니면 null
     */
    public Comment updateComment(Long id, Comment updateParam) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글"));
        comment.updateComment(updateParam);
        return comment;
    }

    /**
     * comment 의 대댓글 먼저 삭제하고, 댓글 삭제 후 삭제한 댓글 갯수 반환
     * @param id commentId
     * @return 삭제한 댓글 갯수
     */
    @Transactional
    public boolean deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글"));
        long originCommentCount = 0; long deletedCommentCount = 0; // 검증용 카운트

        if (comment.getGroupDepth() == 0) {
            // 모댓글 이면 답글 포함 카운트
            originCommentCount = commentQueryRepository.countTotalCommentWithBoardIdAndMEmberId(comment.getBoard().getId(), comment.getId());
            deletedCommentCount = commentRepository.deleteByGroupId(id); // 댓글 + 대댓글삭제
        } else {
            // 대댓글 이면 본인만 삭제
            originCommentCount = 1;
            commentRepository.deleteById(id); // 댓글 삭제
            deletedCommentCount = 1;
        }

        boardRepository.subtractCommentCount(comment.getBoard().getId(), (int) deletedCommentCount); // 댓글 수 감소, count 의 자료형이 int 여야 됨

        log.info("deleteComment() groupDepth = {}, origin={}, deletedComment={}", comment.getGroupDepth(), originCommentCount, deletedCommentCount);
        return originCommentCount == deletedCommentCount;
    }

}
