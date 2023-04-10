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
import java.util.ArrayList;
import java.util.List;

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


    public int deleteImageByStoreImageName(String storeImageName) {
        return imageMapper.deleteImageByStoreImageName(storeImageName);
    }

    /**
     * 삭제할 이미지 리스트를 받아 로컬에서 파일을 삭제
     * @param deleteImageList
     * @return 삭제한 이미지 파일 갯수
     */
    public int deleteImageFromLocal(List<Image> deleteImageList) {
        int count = 0;
        List<File> fileList = new ArrayList<>();
        for (Image image : deleteImageList) {
            fileList.add(new File(image.getImageAddress()));
        }
        for (File file : fileList) {
            boolean isDeleted = file.delete();
            if (isDeleted) count++;
        }

//        log.info("로컬에서 삭제된 파일 = {}", count);
        return count;
    }

    // 출처 https://docs.aws.amazon.com/ko_kr/AmazonS3/latest/userguide/delete-multiple-objects.html
    /**
     * 삭제할 이미지 리스트를 통해 아마존 S3 에서 이미지를 삭제
     * @param deleteImageList
     * @return 삭제된 이미지 갯수
     */
    public int deleteImageFromAmazon(List<Image> deleteImageList, String bucketDir, String innerBucketDir) {

        // 아마존에 전달할 파일 정보 리스트
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();

        // deleteImageList 로 아마존 제거할 리스트 생성
        for (Image image : deleteImageList) {
            keys.add(new DeleteObjectsRequest.KeyVersion(innerBucketDir + image.getStoreImageName()));
        }

        DeleteObjectsRequest multipleDeleteObjectsRequest = new DeleteObjectsRequest(bucketDir).withKeys(keys).withQuiet(false);
        DeleteObjectsResult deleteObjectsResult = null;
        // 아마존에서 제거
        try {
            deleteObjectsResult = amazonS3.deleteObjects(multipleDeleteObjectsRequest);
            log.info("AMAZON S3 {} 개 중 {} 개 제거 완료", deleteImageList.size(), deleteObjectsResult.getDeletedObjects().size());
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        // 믿을수 있는 값인지는 잘 모르겠음...
        return deleteObjectsResult.getDeletedObjects().size();
    }


}
