package hello.board.file;

import hello.board.domain.image.Image;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface ImageStore {

    String getServiceName();
    
    Image storeImage(Long memberId, MultipartFile multipartFile);

}
