package hello.board.domain.repository.mybatis;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import hello.board.domain.image.Image;
import hello.board.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MybatisImageRepository implements ImageRepository {

    private final ImageMapper imageMapper;
    private final AmazonS3 amazonS3;
    // s3 배포용
    @Value("${cloud.aws.s3.bucket}")
    private String bucketDir;

    @Override
    public long saveImage(Image image) {
        imageMapper.saveImage(image);
        return image.getImageId();
    }

    @Override
    public List<Long> saveImageList(List<Image> imageList) {
        List<Long> saveImageIds = new ArrayList<>();
        for (Image image: imageList) {
            imageMapper.saveImage(image);
            saveImageIds.add(image.getImageId());
        }
        return saveImageIds;
    }

    @Override
    public int deleteImage(Long boardId) {

        // 로컬에서 삭제
        List<File> fileList = new ArrayList<>();
        for (Image image :
                imageMapper.findByBoardId(boardId)) {
            fileList.add(new File(image.getImageAddress()));
        }
        int count = deleteFile(fileList);
        log.info("로컬에서 삭제된 이미지 개수 = " + count);

        // DB에서 삭제
        return imageMapper.deleteImage(boardId);
    }


    // 출처 https://docs.aws.amazon.com/ko_kr/AmazonS3/latest/userguide/delete-multiple-objects.html
    public int deleteImageAmazon(Long boardId) {
        // 로컬에서 삭제

        int count = 0;
        
        // 아마존에 전달할 삭제정보
        ArrayList<KeyVersion> keys = new ArrayList<KeyVersion>();
        List<Image> deleteImageList = imageMapper.findByBoardId(boardId);

        deleteImageFromAmazon(deleteImageList);

        // DB에서 삭제
        return imageMapper.deleteImage(boardId);
    }

    @Override
    public Image findById(Long id) {
        return imageMapper.findById(id);
    }

    @Override
    public List<Image> findList(List<Long> imageIds) {
        List<Image> imageList = new ArrayList<>();
        for (Long imageId : imageIds) {
            imageList.add(imageMapper.findById(imageId));
        }
        return imageList;
    }

    @Override
    public List<Image> findByBoardId(Long id) {
        return imageMapper.findByBoardId(id);
    }


    //===================이미지 동기화=======================================================================

    @Override
    // 나중에 로컬에서 영구적으로 이미지 파일을 삭제하기 위해 해당 작업을 함.

    // 원래는 ckeditor 에서 업로드 할때 boardId = 0 으로 한걸 실제 업로드된 uuid 있는것만 boardId 부여해서
    // boardId = boardId -> 실제업로드 / boardId == 0 -> 삭제 했는데

    // 이러면 수정할때 원본이미지 수정할이미지 모두 boardId 가 부여된 상태라 구분이 어려움
    // 수정할때 boardId = 0 으로 set 한다거나 수정할때만 uuid 로 거르거나 하기 어렵거나 번거로워서

    // 그냥 boardId 전부 저장하고 uuid 기준으로 거르기로 함.
    public void syncImage(Long boardId, String[] uploadedImageName) {
        int count = 0;

        // boardId = 0 인 db 이미지에 boardId 부여
        count += imageMapper.setBoardId(boardId);
        log.info("동기화된 이미지 파일 갯수 = " + count);

        // DB 에 등록된 이미지
        List<Image> imageList = imageMapper.findByBoardId(boardId);
        // 실제 업로드된 이미지를 담을 리스트
        List<Image> uploadedImageList = new ArrayList<>();
        
        // 실제로 업로드된 이미지 선별
        for (String storeImageName :
                uploadedImageName) {
            // .get() 대신 .orelse(null) => 없으면 null
            uploadedImageList.add(imageList.stream().filter(image -> image.getStoreImageName().equals(storeImageName)).findFirst().orElse(null));
        }
        // 제거될 이미지리스트
        imageList.removeAll(uploadedImageList);

        // 제거할 imageList 로 제거할 로컬파일 리스트 생성 및 DB 에서 제거
        // 이중포문 두번쓰기 싫어서 이렇게 햇지만 로컬에서 삭제후 DB 에서 삭제가 더 나을지도?
        count = 0;
        List<File> localFileList = new ArrayList<>();
        for (Image image : imageList) {

            // 로컬 파일 리스트 생성
            localFileList.add(new File(image.getImageAddress()));

            // DB에서 제거
            count += imageMapper.deleteImageByStoreImageName(image.getStoreImageName());
        }
        log.info("삭제된 DB 파일 갯수 = {}", count);

        // 로컬 파일 삭제
        count = deleteFile(localFileList);
        log.info("삭제된 로컬 파일 갯수 = " + count);
    }

    // 로컬 파일 삭제
    public int deleteFile(List<File> fileList) {
        int count = 0;
        boolean isDeleted;

        for (File file : fileList) {
            isDeleted = file.delete();
            if (isDeleted) { count++; }
        }

        return count;
    }

    //===============아마존 이미지 동기화=======================================================================


    // 아마존 s3 용 동기화
    // 인터넷에 ckeditor 동기화문제 쳐봤는데 다들 비슷한 방법으로 해결하는듯 (임시폴더에서 옮기거나... 주기적으로 삭제하거나..)

    // 한계: 글쓰기, 수정 해서 사진 업로드 하고 뒤로가기 해서 취소하면, boardId = 0 인 이미지가 삭제되지 않고 스토리지에 남음.

    public void syncImageAmazon(Long boardId, String[] uploadedImageNames) {

        // boardId = 0 인 db 이미지에 boardId 부여
        int result = imageMapper.setBoardId(boardId);
        log.info("setBoardId() 이미지 파일 갯수 = " + result);

        // DB 에 등록된 이미지
        List<Image> imageDbList = imageMapper.findByBoardId(boardId);
        for (Image image : imageDbList) {log.info("ImageList {}", image);}

        // 실제 업로드된 이미지 리스트
        List<Image> uploadedImageList = makeUploadedImageList(uploadedImageNames, imageDbList);
        for (Image image : uploadedImageList) {log.info("uploadedImageList {}" , image);}

        // 제거될 이미지리스트
        List<Image> deleteImageList = makeDeleteImageList(imageDbList, uploadedImageList);
        for (Image image : deleteImageList) {log.info("deleteImageList= {}" , image);}

        // 제거할 이미지가 없음
        if (deleteImageList.isEmpty()) {
            log.info("syncImage 종료 deleteImageList.isEmpty()");
            return;
        }

        // 아마존에서 파일 제거
        deleteImageFromAmazon(deleteImageList);

        // DB에서 제거
        deleteImageFromDb(deleteImageList);

        // 둘중에 하나가 실패하면 같이 실패해야함.

    }


    // Db에 임시 저장된 이미지 중, 요청으로 들어온 이미지 이름(uuid.ext)으로 실제로 업로드된 이미지 선별
    public List<Image> makeUploadedImageList (String[] uploadedImageNames, List<Image> imageDbList) {
        List<Image> uploadedImageList = new ArrayList<>();

        for (String storeImageName : uploadedImageNames) {
            // .get() 대신 .orelse(null) => 없으면 null
            uploadedImageList.add(imageDbList.stream().filter(image -> image.getStoreImageName().equals(storeImageName)).findFirst().orElse(null));
        }
        return uploadedImageList;
    }


    // Db 에 임시 저장된 이미지 리스트 - 실제로 업로드된 이미지 리스트
    public List<Image> makeDeleteImageList(List<Image> imageDbList, List<Image> uploadedImageList) {
        imageDbList.removeAll(uploadedImageList);
        return imageDbList;
    }
    
    // 아마존에서 이미지 파일 제거 , 제거한 count 반환
    public int deleteImageFromAmazon(List<Image> deleteImageList) {
        
        // 아마존에 전달할 파일 정보 리스트
        List<KeyVersion> keys = new ArrayList<>();

        // deleteImageList 로 아마존 제거할 리스트 생성
        for (Image image : deleteImageList) {
            keys.add(new KeyVersion("upload_image/" + image.getStoreImageName()));
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

    // Db에서 이미지 제거, 갯수 반환
    public int deleteImageFromDb(List<Image> deleteImageList) {
        int count = 0;
        for (Image image : deleteImageList) {
            count += imageMapper.deleteImageByStoreImageName(image.getStoreImageName());
        }

        log.info("DB {} 개 중 {} 개 제거 완료", deleteImageList.size(), count);
        return count;
    }

}
