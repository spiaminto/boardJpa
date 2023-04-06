package hello.board.form;

import hello.board.domain.enums.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BoardEditForm {

    private Long id;
    @NotBlank
    @Size(max = 30)
    private String title;
    @NotBlank
    @Size(max = 1500, message = "Content 제한량 초과")
    private String content;

    private String contentLength;
    private String writer;

    // DB 에 진짜로 저장할 이미지url
    private String imageName;
    private Boolean isNotice;

    // Criteria.category 와 동시에 바인딩되는듯
    private Category category;
    

}
