package hello.board.domain.repository;

import hello.board.domain.image.Image;

import java.util.List;

public interface ImageRepository {

    // 이미지 독립적으로 crud 구현후 controller 에서 repository 따로 사용
    // @Transactional ( imagerepo.save, boardrepo.save )

    long saveImage(Image image);

    List<Long> saveImageList(List<Image> imageList);

    int deleteImage(Long boardId);

    int deleteImageAmazon(Long boardId);

    Image findById(Long id);

    List<Image> findList(List<Long> imageIds);

    List<Image> findByBoardId(Long id);

    // 실제 submit 된 이미지 UUID 배열을 받아 DB 에서 동기화(?)
    void syncImage(Long boardId, String[] imageUuid);

    void syncImageAmazon(Long boardId, String[] imageUuid);
    
}
