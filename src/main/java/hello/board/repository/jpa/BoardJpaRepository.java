package hello.board.repository.jpa;

import hello.board.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardJpaRepository extends JpaRepository<Board, Long> {

    List<Board> findByMemberId(Long memberId);

    @Modifying
    @Query("update Board b set b.viewCount = b.viewCount + 1 where b.id = :id")
    void updateViewCount(Long id);

    @Modifying
    @Query("update Board b set b.commentCnt = b.commentCnt + 1 where b.id = :id")
    void addCommentCount(Long id);

    @Modifying
    @Query("update Board b set b.commentCnt = b.commentCnt - :count where b.id = :id")
    void subtractCommentCount(Long id, int count);

    /**
     * Member.username 변경시 해당 Member 의 Board.writer 수정
     * @param memberId
     * @param updateUsername 변경된 username
     * @return 영향받은 rowCount
     */
    @Modifying
    @Query("update Board b set b.writer = :updateUsername where b.member.id = :memberId")
    int updateBoardWriterByMemberId(Long memberId, String updateUsername);






}
