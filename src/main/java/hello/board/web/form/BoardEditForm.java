package hello.board.web.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class BoardEditForm {

    private Long id;
    @NotBlank
    @Size(max = 30)
    private String title;
    @NotBlank
    @Size(max = 500)
    private String content;
    private String writer;

    private LocalDateTime regedate;

    private String isImageModified = "false";
    //private List<MultipartFile> imageFiles;

    // DB 에 진짜로 저장할 이미지url
    private String imageName;


    public BoardEditForm(String title, String content, String writer, List<MultipartFile> imageFiles) {
        this.title = title;
        this.content = content;
        this.writer = writer;
//        this.imageFiles = imageFiles;
    }
}
