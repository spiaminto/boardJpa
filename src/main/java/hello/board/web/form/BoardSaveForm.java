package hello.board.web.form;

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
    @Size(max = 500)
    private String content;
    private String writer;

    // ckEditor 사용하면서 필요없어짐
    // MultipartFile 로 enctype=multipart/form-data 지정된 form 의 첨부파일 자동 바인딩
    // List<MultipartFile> 사용하면 multiple="multiple" 옵션 설정된 파일들 받음
//    private List<MultipartFile> imageFiles;

    // DB 에 진짜로 저장할 이미지url
    private String imageUrl;

    public BoardSaveForm(String title, String content, String writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }
}
