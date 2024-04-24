package hello.board.repository.jpa;

import hello.board.domain.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ImageJpaRepositoryTest {

    @Autowired
    ImageJpaRepository imageJpaRepository;

    @Test
    public void findByBoardId() {
        List<Image> findImages = imageJpaRepository.findByBoardId(17L);// 3개등장
        findImages.forEach(image ->
                log.info(image.toString()));

    }


}