package hello.board.web.controller;

import hello.board.domain.image.Image;
import hello.board.domain.repository.ImageRepository;
import hello.board.web.file.ImageStore;
import hello.board.web.file.ImageStoreAmazon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class ImageController {

    private final ImageStore imageStore;
    private final ImageStoreAmazon imageStoreAmazon;
    private final ImageRepository imageRepository;

    @PostMapping("/image/upload")
    public Map<String, Object> uploadImage(MultipartHttpServletRequest request) throws IOException {

        // name : upload 는 ckeditor 기본 설정인듯.
        List<MultipartFile> uploadImageList = request.getFiles("upload");
        
//        로컬저장
//        List<Image> storedImageList = imageStore.storeImages(uploadImageList);
        
//        아마존 저장
        List<Image> storedImageList = imageStoreAmazon.storeImages(uploadImageList);

        // DB 에 저장 (요청 url 은 저장되지 않음)
        imageRepository.saveImageList(storedImageList);

        // json 응답
        Map<String, Object> result = new HashMap<>();

        result.put("uploaded", true);
        for (Image image : storedImageList) {

            result.put("url", image.getImageAddress());

            // 파일 로컬요청 경로X , 변환된 url 요청 (로컬)
//            result.put("url", image.getImageRequestUrl());
        }

        return result;
    }
}
