package hello.board.file;


import hello.board.domain.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class ImageStoreLocal implements ImageStore{

//     해당 String 의 값을 경로로 지정.
    @Value("C:/Users/felix/.Study/Spring_Practice/board_exfile")
    private String imageDir;

    @Value("/local/image")
    private String imageUrl;


    @Override
    public String getServiceName() {
        return "local";
    }

    // 파일주소: 저장할 디렉토리 + 파일명
    public String createImageAddress(String storeImageName) {return imageDir + "/" + storeImageName;}

    // 요청주소 : WebConfig 에 재정의된 resourceHandler 에서 변환
    public String createImageRequestUrl(String storeImageName) {return imageUrl + "/" + storeImageName;}

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

    /**
     *  보안문제로 파일 절대경로를 직접 사용할수 없음 (not allowed local resourece)
     *      따라서 url 로 변환 후 WebConfig 에서 받을때 절대경로로 수정토록 함.
     *      
     *  요청용 url 및 이미지 정보를 담은 Image 객체 리턴
     */

    // 단일 업로드 CKeditor

    public Image storeImage(Long memberId, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) return null;

        // uploadImageName: 업로드 사진 이름
        String uploadImageName = multipartFile.getOriginalFilename();
        // 서버에 저장할 사진 이름 (uuid + ext)
        String storeImageName = createStoreImageName(uploadImageName);
        // 저장할 경로 + storeImageName + ext
        String imageAddress = createImageAddress(storeImageName);
        // 저장할 요청경로 (직접요청 불가)
        String imageRequestUrl = createImageRequestUrl(storeImageName);

        try {
            // File(경로) 로 파일 쓰기 + IOException
            log.info("imageAddress = {}", imageAddress);
            multipartFile.transferTo(new File(imageAddress));

        } catch (IOException e) {
            log.info("multipartFile.transferTo() IOExeption");
            e.printStackTrace();
        }

        // DB 에 저장을 위한 Image 객체 (boardId = 0L)
        Image image = new Image(uploadImageName, storeImageName, imageAddress, imageRequestUrl, memberId);

        // 사진 정보 담은 Image 객체 반환
        return image;
    }

    // 다중업로드
    // for multipartFile : List multipartFiles, if !multipartFile.isEmpty() storeImage(multipartFile)
    // ckEditor 도입 후 필요X

}
