package hello.board.service;



import hello.board.domain.image.Image;
import hello.board.file.ImageStore;
import hello.board.repository.jpa.BoardJpaRepository;
import hello.board.repository.jpa.ImageJpaRepository;

import hello.board.repository.query.BoardQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageJpaRepository imageRepository;
    private final ImageStore imageStore;

    // amazon s3 접속 주소
    @Value("${cloud.aws.s3.bucket}")
    private String bucketDir;

    @Value("${cloud.aws.s3.bucket.innerDir}")
    private String innerBucketDir;

    /**
     * ckEditor 에서 이미지 파일을 업로드 할때 로컬(아마존) 과 DB 에 이미지를 임시저장
     * @param multipartFile
     * @return 업로드한 이미지 파일로 구성한 Image
     */
    @Transactional
    public Image saveImage(Long id, MultipartFile multipartFile) {
        Image storedImage = imageStore.storeImage(id, multipartFile);
        Image savedImage = saveImageToDb(storedImage);

        log.info("[temp] store 된 이미지 {}, DB.save 된 이미지 {}", storedImage.getId(), savedImage.getId());

        return savedImage;
    }

    protected Image saveImageToDb(Image image) {
        return imageRepository.save(image);
    }

    /**
     * member 가 삭제될때, 해당 member 가 작성한 board 의 이미지 삭제
     * 성공시 true, 실패시 롤백 false
     * @param memberId
     */
    @Transactional
    public boolean deleteImageByMemberId(Long memberId) {
        List<Image> deleteImageList = imageRepository.findByMemberId(memberId);

        if (deleteImageList.isEmpty()) {
            log.info("deleteImageByMemberId, deleteImageList.size = {}", deleteImageList.size());
            return true;
        }

        int fileResult = deleteImageFile(deleteImageList);
        int dbResult = deleteImageFromDb(deleteImageList);

        return fileResult == dbResult ? true : false;
    }

    /**
     * board 가 삭제될때, boardId 를 받아 해당 board 의 image 를 삭제
     * @param boardId
     * @return 로컬(아마존) 과 DB 모두 삭제되고, 그 갯수가 같으면 true
     */
    @Transactional
    public boolean deleteImageByBoardId(Long boardId) {
        List<Image> deleteImageList = imageRepository.findByBoardId(boardId);

        if (deleteImageList.isEmpty()) {
            log.info("deleteImageByBoardId, deleteImageList.size = {}", deleteImageList.size());
            return true;
        }

        int fileResult = deleteImageFile(deleteImageList);
        int dbResult = deleteImageFromDb(deleteImageList);

        return fileResult == dbResult ? true : false;
    }

    /**
     * 삭제할 이미지 리스트를 받아 DB 에서 삭제 (imageIdList로)
     * @param deleteImageList
     * @return 삭제된 행 갯수
     */
    protected int deleteImageFromDb(List<Image> deleteImageList) {
        int count = 0;

        // imageId 만 뽑아서 List 만든 뒤, DB 에서 삭제
        List<Long> imageIdList = deleteImageList.stream().map(Image::getId).collect(Collectors.toList());
        count = imageRepository.deleteByIdList(imageIdList);

        return count;
    }

    // 메서드 삭제예정
    protected int deleteImageFile(List<Image> deleteImageList) {
        return imageStore.deleteImageFiles(deleteImageList);
    }

    /**
     * ckEditor 를 통해 업로드된 이미지중 실제 업로드된 이미지를 선별한 뒤, DB 와 로컬(아마존)에서 동기화
     * @param boardId
     * @param uploadedImageNames
     */
    @Transactional
    public void syncImage(Long memberId, Long boardId, String[] uploadedImageNames) {
        int result;

//        for (String uploadedImageName : uploadedImageNames) {log.info("uploadedImageName {}", uploadedImageName);}

        // memberIdEq, boardId = 0 인 db 이미지에 boardId 부여
        result = imageRepository.setBoardIdAtRawImage(boardId, memberId);
//        log.info("동기화 처리 전 boardId = 0 인 image 갯수 = {} ", result);

        List<Image> imageDbList = imageRepository.findByBoardId(boardId); // DB 에 등록된 이미지 리스트
//        imageDbList.forEach(image -> log.info("ImageList {}", image));

        List<Image> uploadedImageList = makeUploadedImageList(uploadedImageNames, imageDbList); // 실제 업로드된 이미지 리스트
//        uploadedImageList.forEach(image -> log.info("uploadedImageList {}", image));

        List<Image> deleteImageList = makeDeleteImageList(imageDbList, uploadedImageList); // 제거될 이미지리스트
//        deleteImageList.forEach(image -> log.info("deleteImageList= {}", image));

        log.info("임시저장 이미지 개수= {}, 실제 업로드 이미지 개수 = {}, 제거될 이미지 개수 = {}", result, uploadedImageNames.length, deleteImageList.size());

        if (deleteImageList.isEmpty()) {
             // 제거할 이미지가 없음
            log.info("syncImage 종료 deleteImageList.isEmpty()");
            return;
        }

        result = deleteImageFile(deleteImageList); // 파일 제거
        int dbResult = deleteImageFromDb(deleteImageList); // DB 에서 제거

        // 둘중에 하나가 실패하면 같이 실패해야함.
        log.info("syncImage() {} 개 중 로컬(아마존):{} DB:{} 동기화 성공", deleteImageList.size(), result, dbResult);
    }

    /**
     * 실제로 업로드된 이미지 이름 배열로 실제 업로드된 이미지 리스트 생성
     * @param uploadedImageNames
     * @param imageDbList
     * @return 실제로 업로드 된 이미지리스트
     */
    protected List<Image> makeUploadedImageList (String[] uploadedImageNames, List<Image> imageDbList) {
        List<Image> uploadedImageList = new ArrayList<>();

        for (String storeImageName : uploadedImageNames) {
            uploadedImageList.add(imageDbList.stream()
                    .filter(image -> image.getStoreImageName().equals(storeImageName))
                    .findFirst().orElse(null));
        }
        return uploadedImageList;
    }

    /**
     * boardId 로 조회한 이미지 리스트 - 실제로 업로드된 이미지 리스트
     * @param imageDbList
     * @param uploadedImageList
     * @return 삭제할 리스트
     */
    protected List<Image> makeDeleteImageList(List<Image> imageDbList, List<Image> uploadedImageList) {
        imageDbList.removeAll(uploadedImageList);       // equals()
        return imageDbList;
    }


}
