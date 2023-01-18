package hello.board.web.controller;

import hello.board.domain.image.Image;
import hello.board.domain.repository.ImageRepository;
import hello.board.web.file.ImageStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class ImageController {

    private final ImageStore imageStore;
    private final ImageRepository imageRepository;

    @PostMapping("/image/upload")
    public Map<String, Object> uploadImage(MultipartHttpServletRequest request) throws IOException {

        // name : upload 는 ckeditor 기본 설정인듯.
        List<MultipartFile> uploadImageList = request.getFiles("upload");
        List<Image> storedImageList = imageStore.storeImages(uploadImageList);

        // DB 에 저장 (요청 url 은 저장되지 않음)
        imageRepository.saveImageList(storedImageList);

        // json 응답
        Map<String, Object> result = new HashMap<>();

        result.put("uploaded", true);
        for (Image image : storedImageList) {
            // 파일 로컬요청 경로X , 변환된 url 요청
            result.put("url", image.getImageRequestUrl());
        }

        return result;
    }

    // 이미지 다운로드 메소드 (보안성 낮음)
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        // Spring 의 Resource 인터페이스 (구현체 UrlResource) 를 이용해 파일을 컴퓨터에서 찾아 반환
        return new UrlResource("file:" + imageStore.getStorePath(filename));
    }

}
