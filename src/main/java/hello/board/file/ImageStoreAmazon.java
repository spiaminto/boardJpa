package hello.board.file;


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

import java.io.IOException;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
public class ImageStoreAmazon implements ImageStore{

    @Value("${cloud.aws.s3.bucket}")
    private String bucketDir; // s3 bucket 경로
    @Value("${cloud.aws.s3.bucket.innerDir}")
    private String innerBucketDir; // s3 bucket 내부 이미지 저장 경로
    private final AmazonS3 amazonS3;


    @Override
    public String getServiceName() {
        return "amazonS3";
    }

    /**
     * 이미지 파일의 S3 주소 받아오기
     */
    public String createImageAddress(String storeImageName) {
        return amazonS3.getUrl(bucketDir, innerBucketDir + storeImageName).toString();
    }

    /**
     * 서버에 저장할 파일 이름 생성
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
        int position = originalImageName.lastIndexOf(".");
        String ext = originalImageName.substring(position);
        return ext;
    }

    public Image storeImage(Long memberId, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) return null;
        String contentType = "";

        String uploadImageName = multipartFile.getOriginalFilename();
        String storeImageName = createStoreImageName(uploadImageName);

        //content type을 지정하지 않으면 자동으로 "application/octet-stream"으로 고정
        switch (extractExt(multipartFile.getOriginalFilename())) {
            case "jpeg": case "jpg" :
                contentType = "image/jpeg";
                break;
            case "png":
                contentType = "image/png";
                break;
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType); // 아마존 파일에 metadata 를 반드시 지정하라고 적혀있음.
            metadata.setContentLength(multipartFile.getSize()); // contentLength 지정안하면 콘솔에 메시지뜸.

            amazonS3.putObject(new PutObjectRequest(bucketDir, innerBucketDir + storeImageName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            
        // 아마존 doc 에 있던 예외처리 + IOEx
        } catch (AmazonServiceException e) {
            log.info("storeImage exception = {}, message = {}", e, e.getMessage());
        } catch (SdkClientException e) {
            log.info("storeImage exception = {}, message = {}", e, e.getMessage());
        } catch (IOException e) {
            log.info("storeImage exception = {}, message = {}", e, e.getMessage());
            e.printStackTrace();
        }

        String imageAddress = createImageAddress(storeImageName);

        // DB 에 저장용 Image (boardId = 0)
        Image image = Image.builder()
                .uploadImageName(uploadImageName)
                .storeImageName(storeImageName)
                .imageAddress(imageAddress)
                .imageRequestUrl(imageAddress) // imageRequestUrl = imageAddress
                .memberId(memberId).build();

        return image;
    }

}
