package hello.board.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import hello.board.domain.board.Board;
import hello.board.domain.criteria.Criteria;
import hello.board.domain.image.Image;
import hello.board.file.ImageStore;
import hello.board.file.ImageStoreAmazon;
import hello.board.repository.BoardRepository;
import hello.board.repository.ImageRepository;
import hello.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ImageStore imageStore;
    private final ImageStoreAmazon imageStoreAmazon;
    private final AmazonS3 amazonS3;

    // 스프링 active profile
    @Value("${spring.profiles.active}")
    private String activeProfile;

    // amazon s3 접속 주소
    @Value("${cloud.aws.s3.bucket}")
    private String bucketDir;

    /**
     * ckEditor 에서 이미지 파일을 업로드 할때 로컬(아마존) 과 DB 에 이미지를 임시저장
     * @param multipartFile
     * @return 업로드한 이미지 파일로 구성한 Image
     */
    public Image saveImage(MultipartFile multipartFile) {
        Image storedImage;
        if (activeProfile.contains("S3")) {
            storedImage = imageStoreAmazon.storeImage(multipartFile);
        } else {
            storedImage = imageStore.storeImage(multipartFile);
        }

        Image savedImage = saveImageToDb(storedImage);
        log.info("store 된 이미지 {}, DB.save 된 이미지 {}", storedImage.getImageId(), savedImage.getImageId());

        // 예외처리

        return savedImage;
    }

    public Image saveImageToDb(Image image) {
        return imageRepository.saveImage(image);
    }

    /**
     * board 가 삭제될때, boardId 를 받아 해당 board 의 image 를 삭제
     * @param boardId
     * @return 로컬(아마존) 과 DB 모두 삭제되고, 그 갯수가 같으면 true
     */
    public boolean deleteImageByBoardId(Long boardId) {
        List<Image> deleteImageList = imageRepository.findByBoardId(boardId);
        int result = 0;
        if (activeProfile.contains("S3")) {
            result = deleteImageFromAmazon(deleteImageList);
        } else {
            result = deleteImageFromLocal(deleteImageList);
        }
        int dbResult = deleteImageFromDb(deleteImageList);

        return result == dbResult ? true : false;
    }

    public void deleteImageByMemberId(Long memberId) {
        Criteria criteria = new Criteria();
        
        // 글 갯수가 9999개 이상이면 전부 삭제되지 않음
        criteria.setContentPerPage(9999);

        List<Board> boardList = boardRepository.findPagedBoardWithMemberId(criteria, memberId);
        boardList.forEach(board -> deleteImageByBoardId(board.getId()));
    }

    /**
     * 삭제할 이미지 리스트를 받아 DB 에서 삭제 (storeImageName 으로)
     * @param deleteImageList
     * @return 삭제된 행 갯수
     */
    public int deleteImageFromDb(List<Image> deleteImageList) {
        int count = 0;
        for (Image image : deleteImageList) {
            count += imageRepository.deleteImageByStoreImageName(image.getStoreImageName());
        }

//        log.info("DB {} 개 중 {} 개 제거 완료", deleteImageList.size(), count);
        return count;
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
    public int deleteImageFromAmazon(List<Image> deleteImageList) {

        // 아마존에 전달할 파일 정보 리스트
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();

        // deleteImageList 로 아마존 제거할 리스트 생성
        for (Image image : deleteImageList) {
            keys.add(new DeleteObjectsRequest.KeyVersion("upload_image/" + image.getStoreImageName()));
        }

        DeleteObjectsRequest multipleDeleteObjectsRequest = new DeleteObjectsRequest(bucketDir).withKeys(keys).withQuiet(false);
        DeleteObjectsResult deleteObjectsResult = null;
        // 아마존에서 제거
        try {
            deleteObjectsResult = amazonS3.deleteObjects(multipleDeleteObjectsRequest);
//            log.info("AMAZON S3 {} 개 중 {} 개 제거 완료", deleteImageList.size(), deleteObjectsResult.getDeletedObjects().size());
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

    // 이미지 동기화
    // ckeditor 에서 업로드시 image.boardId = 0 으로 업로드. 우선 해당 boardId 를 set.
    //  이후, 실제로 업로드 된 image 들의 image.storeImageName 을 가져와
    //   Db 와 로컬(아마존) 에서 대조 후 동기화.

    /**
     * ckEditor 를 통해 업로드된 이미지중 실제 업로드된 이미지를 선별한 뒤, DB 와 로컬(아마존)에서 동기화
     * @param boardId
     * @param uploadedImageNames
     */
    public void syncImage(Long boardId, String[] uploadedImageNames) {

        // boardId = 0 인 db 이미지에 boardId 부여
        int result = imageRepository.setBoardId(boardId);
        log.info("동기화 처리 전 boardId = 0 인 image 갯수 = {} ", result);

        // DB 에 등록된 이미지
        List<Image> imageDbList = imageRepository.findByBoardId(boardId);
//        for (Image image : imageDbList) {log.info("ImageList {}", image);}

        // 실제 업로드된 이미지 리스트
        List<Image> uploadedImageList = makeUploadedImageList(uploadedImageNames, imageDbList);
//        for (Image image : uploadedImageList) {log.info("uploadedImageList {}" , image);}

        // 제거될 이미지리스트
        List<Image> deleteImageList = makeDeleteImageList(imageDbList, uploadedImageList);
//        for (Image image : deleteImageList) {log.info("deleteImageList= {}" , image);}

        log.info("실제로 업로드 된 이미지 수 = {}, 제거될 이미지 수 = {}", uploadedImageList.size(), deleteImageList.size());

        // 제거할 이미지가 없음
        if (deleteImageList.isEmpty()) {
            log.info("syncImage 종료 deleteImageList.isEmpty()");
            return;
        }

        if (activeProfile.contains("S3")) {
            // 아마존에서 파일 제거
            result = deleteImageFromAmazon(deleteImageList);
        } else {
            // 로컬에서 파일 제거
            result = deleteImageFromLocal(deleteImageList);
        }

        // DB에서 제거
        int dbResult = deleteImageFromDb(deleteImageList);

        // 둘중에 하나가 실패하면 같이 실패해야함.

        log.info("syncImage() {} 개 중 로컬(아마존):{} DB:{} 동기화 성공", deleteImageList.size(), result, dbResult);
    }

    /**
     * 실제로 업로드된 이미지 이름 배열로 실제 업로드된 이미지 리스트 생성
     * @param uploadedImageNames
     * @param imageDbList
     * @return 실제로 업로드 된 이미지리스트
     */
    public List<Image> makeUploadedImageList (String[] uploadedImageNames, List<Image> imageDbList) {
        List<Image> uploadedImageList = new ArrayList<>();

        for (String storeImageName : uploadedImageNames) {
            // .get() 대신 .orelse(null) => 없으면 null
            uploadedImageList.add(imageDbList.stream().filter(image -> image.getStoreImageName().equals(storeImageName)).findFirst().orElse(null));
        }
        return uploadedImageList;
    }

    /**
     * boardId 로 조회한 이미지 리스트 - 실제로 업로드된 이미지 리스트
     * @param imageDbList
     * @param uploadedImageList
     * @return 삭제할 리스트
     */
    public List<Image> makeDeleteImageList(List<Image> imageDbList, List<Image> uploadedImageList) {
        imageDbList.removeAll(uploadedImageList);
        return imageDbList;
    }




}
