package hello.board.web.file;


import hello.board.domain.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class ImageStore {

    // 해당 String 의 값을 경로로 지정.
    @Value("C:/Users/felix/.Study/Spring_Practice/board_exfile")
    private String imageDir;

    @Value("/images/request")
    private String imageUrl;

    // 저장할 디렉토리 + 파일명
    public String getStorePath(String storeImageName) {return imageDir + "/" + storeImageName;}
    public String getStorePathForCk(String storeImageName) {return imageUrl + "/" + storeImageName;}

    // 서버에 저장할 파일명 작성
    public String createStoreImageName(String originalImageName) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalImageName);
        return uuid + ext;
    }

    // 확장자 추출
    public String extractExt(String originalImageName) {
        // 마지막. 의 index(position)
        int position = originalImageName.lastIndexOf(".");
        // 해당 position 기준으로 자름. (position 포함)
        String ext = originalImageName.substring(position);
        return ext;
    }

    // 단일업로드
    /*
    public Image storeImage(MultipartFile multipartFile, Long boardId) throws IOException {
        if (multipartFile.isEmpty()) return null;

        // 업로드 사진 이름
        String originalImageName = multipartFile.getOriginalFilename();
        // 서버에 저장할 사진 이름
        String storeImageName = createStoreImageName(originalImageName);
        // 저장할 경로(+파일명)
        String storePath = getStorePath(storeImageName);


        // File(경로) 로 파일 쓰기 + IOException
        log.info("storePath = {}", storePath);
        multipartFile.transferTo(new File(storePath));
        
        // 사진 정보 담은 Image 객체 반환
        return new Image(boardId, originalImageName, storeImageName, storePath);
    }
    
    // 다중업로드
    public List<Image> storeImages(List<MultipartFile> multipartFiles, Long boardId) throws IOException {
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                imageList.add(storeImage(multipartFile, boardId));
            }
        }
        return imageList;
    }

     */

    /**
     * 원래는 DB 저장을 위해 Image 를 파일로 쓴 뒤, 파일 경로및 기타 정보를 담은 Image 객체를 리턴했으나,
     * CK editor 적용 후, 일단 파일 경로만 리턴하도록 수정.
     *
     * -> 보안문제로 파일 절대경로를 직접 사용할수 없음 (not allowed local resourece)
     *      따라서 url 로 변환 후 WebConfig 에서 받을때 절대경로로 수정토록 함.
     *      
     *  일단 DB 저장 및 관리를 위해 변환된 url 과 Image 객체를 모두 리턴
     */

    // 단일 업로드 CKeditor
    public Image storeImage(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) return null;

        // 업로드 사진 이름
        String originalImageName = multipartFile.getOriginalFilename();
        // 서버에 저장할 사진 이름
        String storeImageName = createStoreImageName(originalImageName);
        // 저장할 경로(+파일명)
        String storePath = getStorePath(storeImageName);

        // 저장할 요청경로
        String storeUrl = getStorePathForCk(storeImageName);

        // File(경로) 로 파일 쓰기 + IOException
        log.info("storePath = {}", storePath);
        multipartFile.transferTo(new File(storePath));

        // DB 에 저장을 위한 Image 객체 (boardId = 0L)
        Image image = new Image(originalImageName, storeImageName, storePath, storeUrl);

        // 사진 정보 담은 Image 객체 반환
        return image;
    }

    // 다중업로드
    public List<Image> storeImages(List<MultipartFile> multipartFiles) throws IOException {
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                imageList.add(storeImage(multipartFile));
            }
        }
        return imageList;
    }

}
