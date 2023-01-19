package hello.board.domain.repository.mybatis;

import hello.board.domain.image.Image;
import hello.board.domain.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class MybatisImageRepository implements ImageRepository {

    private final ImageMapper imageMapper;

    public MybatisImageRepository(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

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
    public int deleteImage(long boardId) {

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
            count += imageMapper.deleteImageByStoreImageName(boardId, image.getStoreImageName());
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
}
