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

    @Value("C:/Users/felix/.Study/Spring_Practice/board_exfile")
    private String imageDir;

    @Value("/local/image")
    private String imageUrl;

    @Override
    public String getServiceName() {
        return "local";
    }

    /**
     * 로컬에 저장된 파일 주소 생성
     */
    public String createImageAddress(String storeImageName) {return imageDir + "/" + storeImageName;}

    /**
     * 브라우저에서 요청할 url.
     * 보안문제?로 파일 절대경로를 직접 사용할수 없음 (not allowed local resource)
     */
    public String createImageRequestUrl(String storeImageName) {return imageUrl + "/" + storeImageName;}

    /**
     * 로컬(서버)에 저장할 파일 이름 생성
     */
    public String createStoreImageName(String originalImageName) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalImageName);
        return uuid + ext;
    }

    /**
     * 확장자 추출
     */
    public String extractExt(String originalImageName) {
        // 마지막. 의 index(position)
        int position = originalImageName.lastIndexOf(".");
        // 해당 position 기준으로 자름. (position 포함)
        String ext = originalImageName.substring(position);
        return ext;
    }

    public Image storeImage(Long memberId, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) return null;

        String uploadImageName = multipartFile.getOriginalFilename();
        String storeImageName = createStoreImageName(uploadImageName);
        String imageAddress = createImageAddress(storeImageName);
        String imageRequestUrl = createImageRequestUrl(storeImageName);

        try {
            multipartFile.transferTo(new File(imageAddress));
        } catch (IOException e) {
            log.info("Exception multipartFile.transferTo() e={}", e);
        }

        // DB 에 저장용 Image (boardId = 0L)
        Image image = Image.builder()
                .uploadImageName(uploadImageName)
                .storeImageName(storeImageName)
                .imageAddress(imageAddress)
                .imageRequestUrl(imageRequestUrl)
                .memberId(memberId).build();

        return image;
    }

}
