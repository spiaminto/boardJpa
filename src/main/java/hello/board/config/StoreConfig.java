package hello.board.config;

import com.amazonaws.services.s3.AmazonS3;
import hello.board.file.ImageStore;
import hello.board.file.ImageStoreAmazon;
import hello.board.file.ImageStoreLocal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StoreConfig {

    private final AmazonS3 amazonS3;

    // 스프링 active profile
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public ImageStore imageStore() {
        log.info("imageStore()");
        if (activeProfile.contains("S3")) {
            return new ImageStoreAmazon(amazonS3);
        } else {
            return new ImageStoreLocal();
        }
    }

}
