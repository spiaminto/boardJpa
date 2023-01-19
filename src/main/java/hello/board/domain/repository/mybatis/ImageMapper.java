package hello.board.domain.repository.mybatis;

import hello.board.domain.image.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImageMapper {

    void saveImage(Image image);

    int deleteImage(Long boardId);

    Image findById(Long imageId);

    List<Image> findByBoardId(Long boardId);
    
    // ckeditor 로 업로드된 모든 이미지(실제이미지 + 임시이미지)에 boardId 지정
    int setBoardId(Long boardId);

    // 임시이미지 삭제용
    int deleteImageByStoreImageName(@Param("boardId") Long boardId, @Param("storeImageName") String storeImageName);

}
