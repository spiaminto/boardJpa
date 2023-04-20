package hello.board.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import hello.board.domain.image.Image;
import hello.board.repository.mybatis.ImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ImageRepository {

    private final ImageMapper imageMapper;
    private final AmazonS3 amazonS3;

    public Image findById(Long id) {
        return imageMapper.findById(id);
    }

    public List<Image> findByBoardId(Long id) {
        return imageMapper.findByBoardId(id);
    }

    public int setBoardId(Long memberId, Long boardId) {
        return imageMapper.setBoardId(memberId, boardId);
    }


    public Image saveImage(Image image) {
        imageMapper.saveImage(image);
        return image;
    }


    public int deleteImageByIdList(List<Long> imageIdList) {
        return imageMapper.deleteImageByIdList(imageIdList);
    }

    /**
     * 삭제할 이미지 리스트를 받아 로컬에서 파일을 삭제
     * @param deleteImageList
     * @return 삭제한 이미지 파일 갯수
     */
    public int deleteImageFromLocal(List<Image> deleteImageList) {
        int count = 0;

        List<File> fileList = deleteImageList.stream()
                .map(Image -> new File(Image.getImageAddress())).collect(Collectors.toList());

        for (File file : fileList) {
            boolean isDeleted = file.delete();
            if (isDeleted) count++;
        }

//        log.info("로컬에서 삭제된 파일 = {}", count);
        return count;
    }

    /**
     * 삭제할 이미지 리스트를 통해 아마존 S3 에서 이미지를 삭제.
     * @param deleteImageList
     * @return 삭제된 이미지 갯수
     */
    public int deleteImageFromAmazon(List<Image> deleteImageList, String bucketDir, String innerBucketDir) {

        // 아마존에 전달할 파일 정보 리스트, DeleteObjectRequest.KeyVersion
        List<KeyVersion> keys = deleteImageList.stream()
                .map(image -> new KeyVersion(innerBucketDir + image.getStoreImageName()))
                .collect(Collectors.toList());

        DeleteObjectsRequest multipleDeleteObjectsRequest = new DeleteObjectsRequest(bucketDir).withKeys(keys).withQuiet(false);
        DeleteObjectsResult deleteObjectsResult = null;

        try {
            deleteObjectsResult = amazonS3.deleteObjects(multipleDeleteObjectsRequest);
            log.info("AMAZON S3 {} 개 중 {} 개 제거 완료", deleteImageList.size(), deleteObjectsResult.getDeletedObjects().size());
        } catch (AmazonServiceException e) {
            // Docs) The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
            log.info("deleteImageFromAmazon() e={}", e);
        } catch (SdkClientException e) {
            // Docs) Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
            log.info("deleteImageFromAmazon() e={}", e);
        }

        return deleteObjectsResult.getDeletedObjects().size();
    }


}
