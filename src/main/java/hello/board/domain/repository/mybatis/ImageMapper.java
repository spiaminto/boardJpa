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

    // 원래는 실제 업로드 uuid 존재하면 boardId 주려고 했는데
    // 수정코드 작성하다 보니 그냥 boardID=0 에다가 전부 boardId 주고, uuid 로 거르는게 낫다고 판단.
    // 그래야 수정할때도 uuid 로 거를수 있음
    // (수정할때는 원본이미지, 수정이미지 모두 boardId 가 부여된 상태라 boardId == 0 으로 거를수가 없음)
//    int syncImage(@Param("boardId") Long boardId, @Param("storeImageName") String storeImageName);
    int syncImage(Long boardId);

    int deleteImageByStoreImageName(@Param("boardId") Long boardId, @Param("storeImageName") String storeImageName);

}
