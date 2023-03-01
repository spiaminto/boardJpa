package hello.board.repository.mybatis;

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
    
    // ckeditor 로 업로드된 모든 이미지(실제이미지 + 임시이미지)를 memberId 로 거른뒤, boardId 지정
    //  memberId 로 안거르면, 두명이상이 동시에 글 작성시 문제생김
    int setBoardId(@Param("memberId") Long memberId, @Param("boardId") Long boardId);

    // 임시이미지 삭제용
    int deleteImageByStoreImageName(@Param("storeImageName") String storeImageName);

}
