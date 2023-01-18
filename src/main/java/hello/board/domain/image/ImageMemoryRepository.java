package hello.board.domain.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ImageMemoryRepository {

    private static final Map<Long, Image> imageRepository = new HashMap<>();
    private static long sequence = 0L;

    // read
    public Image findById (Long id) {
        return imageRepository.get(id);
    }

    public List<Image> findList (List<Long> imageIds) {
        List<Image> imageList = new ArrayList<>();
        for (long id : imageIds) {
            imageList.add(findById(id));
        }
        return imageList;
    }

    // create
    public long saveImage(Image image) {
        if (image == null) return 0;

        imageRepository.put(++sequence, image);
        return sequence;
    }

    public List<Long> saveImageList(List<Image> imageList) {
        List<Long> saveIds = new ArrayList<>();
        for (Image image : imageList) {
            saveIds.add(saveImage(image));
        }
        return saveIds;
    }

    public void deleteImage(long id) {
        // 이미지가 repository 에 없을경우 (일반적이진 않을듯)
        if (null == imageRepository.get(id)) {
            log.info("ImageRepository deleteImage() null == image, id = {}", id);
            return;
        }
        imageRepository.remove(id);
    }

    public void deleteImageList(List<Long> imageIdList) {
        for (long id : imageIdList) {
            deleteImage(id);
        }
    }

    // delete


}
