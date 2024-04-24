package hello.board.repository.jpa;

import hello.board.domain.board.Board;
import hello.board.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    // 게시글의 특정 댓글 + 해당 대댓글 의 갯수
    long countByBoardIdEqualsAndGroupIdEquals(Long boardId, Long groupId);

    /**
     * memberId 를 받아 target_id 와 대조하여 count. sync 및 삭제시 로그확인용으로 사용한듯.
     * @param memberId: targetId 와 대조할 memberId
     * @return
     */
    long countByTargetIdEquals(Long memberId);
    long countByMemberIdEquals(Long memberId);

    List<Comment> findByBoardId(Long boardId);

    /**
     * Member.username 변경시 해당 Member 의 Comment.writer 수정
     * @param memberId
     * @param updateUsername 변경된 username
     * @return 영향받은 rowCount
     */
    @Modifying
    @Query("update Comment c set c.writer = :updateUsername where c.memberId = :memberId")
    int updateCommentWriterByMemberId(Long memberId, String updateUsername);

    /**
     * Member.username 변경시 해당 Member 의 Comment.target 수정
     * @param memberId
     * @param updateUsername 변경된 username
     * @return 영향받은 rowCount
     */
    @Modifying
    @Query("update Comment c set c.target = :updateUsername where c.targetId = :memberId")
    int updateTargetByMemberId(Long memberId, String updateUsername);

    // 댓글을 boardId 로 필터링 하여 제거
    int deleteByBoardId(Long boardId);
    // 대댓글을 모댓글 id 로 필터링 하여 제거
    int deleteByGroupId(Long groupId);

    // 대댓글을 targetId 로 필터링 하여 제거 (select 후, commentId 마다 delete 치길래 쿼리 직접작성)
    @Modifying
    @Query("delete Comment c where c.targetId = :targetId")
    int deleteByTargetId(Long targetId);

    // 테스트용
    List<Comment> findByMemberId(Long memberId);
    List<Comment> findByTargetId(Long targetId);

}
