package hello.board.web.form;

import hello.board.domain.enums.Category;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardSaveForm {

    @NotBlank
    @Size(max = 30)
    private String title;
    @NotBlank
    @Size(max = 1000)
    private String content;
    private String writer;

    // DB 에 진짜로 저장할 이미지url
    private String imageName;

    private Category category;

}
