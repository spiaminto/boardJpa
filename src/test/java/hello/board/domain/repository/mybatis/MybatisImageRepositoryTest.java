package hello.board.domain.repository.mybatis;

import hello.board.domain.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j

/**
 * 테스트는 아직 imageId <-> boardId 변환 안됫음
 */
class MybatisImageRepositoryTest {

    @Autowired
    MybatisImageRepository imageRepository;

    Image image = new Image(9999L, "uploadName", UUID.randomUUID().toString() + ".jpg", "server/test/");
    Image image2 = new Image(9998L, "uploadName2", UUID.randomUUID().toString(), "server/test/");
    Image image3 = new Image(9997L, "uploadName2", UUID.randomUUID().toString(), "server/test/");
    Image image4 = new Image(9996L, "uploadName3", UUID.randomUUID().toString(), "/server/test2/");
    List<Image> imageList = Arrays.asList(image, image2, image3, image4);

    @Test
    void saveImage() {
        String storeImageName = image.getStoreImageName();
        imageRepository.saveImage(image);
    }

    @Test
    void saveImageList() {
        imageRepository.saveImageList(imageList);
        for (Image image : imageList)
            log.info(image.getImageId() + " image = {}", image.toString() );
    }

    @Test
    void deleteImage() {
        imageRepository.saveImage(image);
        imageRepository.deleteImage(image.getBoardId());
    }

    @Test
    void findById() {
        imageRepository.saveImage(image);
        Image findImage = imageRepository.findById(image.getImageId());
        assertThat(findImage.equals(image));
    }

    @Test
    void findList() {
        imageRepository.saveImageList(imageList);
        List<Long> imageIds = new ArrayList<>();
        for (Image image : imageList)
            imageIds.add(image.getImageId());
        imageRepository.findList(imageIds);
    }

    @Test
    void findByBoardId() {
        imageRepository.saveImageList(imageList);
        imageRepository.findByBoardId(3L);
    }
}