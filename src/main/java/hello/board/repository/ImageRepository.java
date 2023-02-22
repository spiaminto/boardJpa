package hello.board.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import hello.board.domain.image.Image;
import hello.board.repository.mybatis.ImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ImageRepository {

    private final ImageMapper imageMapper;

    public Image findById(Long id) {
        return imageMapper.findById(id);
    }

    public List<Image> findByBoardId(Long id) {
        return imageMapper.findByBoardId(id);
    }


    public int setBoardId(Long boardId) {
        return imageMapper.setBoardId(boardId);
    }


    public Image saveImage(Image image) {
        imageMapper.saveImage(image);
        return image;
    }


    public int deleteImageByStoreImageName(String storeImageName) {
        return imageMapper.deleteImageByStoreImageName(storeImageName);
    }


}
