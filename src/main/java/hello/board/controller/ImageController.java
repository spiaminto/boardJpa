package hello.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.JSONStringUtils;
import hello.board.auth.PrincipalDetails;
import hello.board.domain.image.Image;
import hello.board.file.ImageStoreLocal;
import hello.board.repository.ImageRepository;
import hello.board.file.ImageStoreAmazon;
import hello.board.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        
        // json 응답용
        Map<String, Object> result = new HashMap<>();

        // "upload" 가 ckeditor 기본설정.
        MultipartFile uploadImage = request.getFile("upload");

        long size = uploadImage.getSize();
        // Byte 단위 size 를 MB 로 바꾸고, 100 곱해서 반올림후 100 나누기. -> 1.23 (MB)
        double sizeMega = Math.round(size / 1048576d * 100) / 100d;
//        log.info("sizemega = {}", sizeMega);
        
        // 이미지 사이즈 체크, 3MB 초과
        if (!imageSizeCheck(size)) {
            result.put("uploaded", 0);

            Map<String, String> error = new HashMap<>();
            error.put("message", "3MB 보다 큰 이미지는 업로드 할 수 없습니다. 현재 이미지: " + sizeMega + "MB");
            result.put("error", error);

//            log.info("result {}", result);
            return result;
        }

        // 이미지 저장
        Image savedImage = imageService.saveImage(memberId, uploadImage);
        result.put("uploaded", 1);

        // local 에 쓰면 requestUrl, amazon 에 쓰면 address = requestUrl
        result.put("url", savedImage.getImageRequestUrl());

        return result;
    }

    /**
     * multipartfile.size() 를 받아 3MB 미만이면 true, 초과면 false
     */
    public boolean imageSizeCheck(long size) {
        double sizeMega = Math.round(size / 1048576d * 100) / 100d;
        if (sizeMega > 3d) {
            log.info("size is larger than 3MB, size={}", sizeMega);
            return false;
        } else {
            return true;
        }
    }
}
