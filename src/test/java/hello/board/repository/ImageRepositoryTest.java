package hello.board.repository;

import hello.board.domain.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j

/**
 * 테스트는 아직 imageId <-> boardId 변환 안됫음
 */
class ImageRepositoryTest {

    @Autowired
    ImageRepository imageRepository;

    //    Image image = new Image(9999L, "uploadName", UUID.randomUUID().toString() + ".jpg", "server/test/");
    Image image = Image.builder().uploadImageName("uploadName").storeImageName(UUID.randomUUID() + "jpg")
        .imageAddress("/server/test/").memberId(9999L).build();
    Image image2 = Image.builder().uploadImageName("uploadName2").storeImageName(UUID.randomUUID() + "jpg")
            .imageAddress("/server/test/").build();
    Image image3 = Image.builder().uploadImageName("uploadName3").storeImageName(UUID.randomUUID() + "jpg")
            .imageAddress("/server/test/").build();
    Image image4 = Image.builder().uploadImageName("uploadName4").storeImageName(UUID.randomUUID() + "jpg")
            .imageAddress("/server/test/").build();
    List<Image> imageList = Arrays.asList(image, image2, image3, image4);

    @Test
    void saveImage() {
        String storeImageName = image.getStoreImageName();
        imageRepository.saveImage(image);
    }

//    @Test
//    void saveImageList() {
//        imageRepository.saveImageList(imageList);
//        for (Image image : imageList)
//            log.info(image.getImageId() + " image = {}", image.toString() );
//    }

//    @Test
//    void deleteImage() {
//        imageRepository.saveImage(image);
//        imageRepository.deleteImage(image.getBoardId());
//    }

    @Test
    void findById() {
        imageRepository.saveImage(image);
        Image findImage = imageRepository.findById(image.getImageId());
        assertThat(findImage.equals(image));
    }

//    @Test
//    void findList() {
//        for (Image image : imageList) {
//            imageRepository.saveImage(image);
//        }
//        List<Long> imageIds = new ArrayList<>();
//        for (Image image : imageList)
//            imageIds.add(image.getImageId());
//        imageRepository.findList(imageIds);
//    }

    @Test
    void findByBoardId() {
        for (Image image : imageList) {
            imageRepository.saveImage(image);
        }
        imageRepository.findByBoardId(3L);
    }
}