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

// @Configuration 을 통해 등록되는 스프링 빈은 new 로 생성해도, 스프링에서 싱글톤으로 관리된다. (CGLIB)
@Configuration
@RequiredArgsConstructor
@Slf4j
public class StoreConfig {

    private final AmazonS3 amazonS3;

    @Value("${spring.profiles.active}")
    private String activeProfile; // 스프링 active profile

    @Bean
    public ImageStore imageStore() {
//        log.info("imageStore(), activeProfile={}", activeProfile);
        if (activeProfile.contains("prod")) {
            return new ImageStoreAmazon(amazonS3);
        } else {
            return new ImageStoreLocal();
        }
    }

}
