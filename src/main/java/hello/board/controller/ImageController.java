package hello.board.controller;

import hello.board.auth.PrincipalDetails;
import hello.board.domain.image.Image;
import hello.board.file.ImageStoreLocal;
import hello.board.repository.ImageRepository;
import hello.board.file.ImageStoreAmazon;
import hello.board.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final ImageService imageService;

    // ck editor 는 여러 이미지를 동시에 올려도, uploadImage() 를 이미지 마다 따로 실행한다.
    @PostMapping("/image")
    public Map<String, Object> uploadImage(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                           MultipartHttpServletRequest request) throws IOException {

        Long memberId = principalDetails.getMember().getId();

        // name : upload 는 ckeditor 기본 설정인듯.
        MultipartFile uploadImage = request.getFile("upload");

        Image savedImage = imageService.saveImage(memberId, uploadImage);

        // json 응답
        Map<String, Object> result = new HashMap<>();

        result.put("uploaded", true);

        // local 에 쓰면 requestUrl, amazon 에 쓰면 address = requestUrl
        result.put("url", savedImage.getImageRequestUrl());

        return result;
    }
}
