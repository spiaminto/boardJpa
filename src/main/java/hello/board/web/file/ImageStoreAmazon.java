package hello.board.web.file;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import hello.board.domain.image.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageStoreAmazon {

    // s3 배포용
    @Value("${cloud.aws.s3.bucket}/upload_image")
    private String bucketDir;

    private final AmazonS3 amazonS3;

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

    // 단일 업로드 CKeditor
    public Image storeImage(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) return null;
        String contentType = "";

        // uploadImageName: 업로드 사진 이름
        String uploadImageName = multipartFile.getOriginalFilename();
        // 서버에 저장할 사진 이름 (uuid + ext)
        String storeImageName = createStoreImageName(uploadImageName);

        //content type을 지정해서 올려주지 않으면 자동으로 "application/octet-stream"으로 고정되어
        // 링크 클릭시 웹에서 열리는게 아니라 자동 다운이 시작됨.
        switch (extractExt(multipartFile.getOriginalFilename())) {
            case "jpeg":
                contentType = "image/jpeg";
                break;
            case "png":
                contentType = "image/png";
                break;
            case "txt":
                contentType = "text/plain";
                break;
            case "csv":
                contentType = "text/csv";
                break;
        }

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            log.info(multipartFile.getInputStream().toString().substring(0, 20));

            // CannedAccessControlList public 으로 설정해야 모두 접근가능
            amazonS3.putObject(new PutObjectRequest(bucketDir, storeImageName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        // amazonS3 에 저장된 이미지의 url
        String imageAddress = amazonS3.getUrl(bucketDir, storeImageName).toString();

        // DB 에 저장을 위한 Image 객체 (boardId = 0L)
        // 일단 임시로 request url 을 동일하게 설정했음
        Image image = new Image(uploadImageName, storeImageName, imageAddress, imageAddress);

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
