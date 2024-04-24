package hello.board.repository.jpa;

import hello.board.domain.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {

    List<Image> findByBoardId(Long boardId);
    List<Image> findByMemberId(Long memberId); // 멤버아이디로 image 삭제시 사용

    /**
     * imageIds 로 이미지 삭제
     * @param longs
     */
    void deleteAllByIdInBatch(Iterable<Long> longs);

    @Modifying
    @Query("delete from Image i where i.id in :imageIds")
    int deleteByIdList(List<Long> imageIds);

    @Modifying
    @Query("update from Image i set i.boardId = :boardId where i.boardId = 0 and i.memberId = :memberId")
    int setBoardIdAtRawImage(Long boardId, Long memberId);

}
