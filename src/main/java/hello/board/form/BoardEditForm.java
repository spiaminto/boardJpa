package hello.board.form;

import hello.board.domain.enums.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class BoardEditForm {

    private Long id;
    @NotBlank
    @Size(max = 30)
    private String title;
    @NotBlank
    @Size(max = 2200, message = "Content 제한량 초과") // 이미지 5장 기준 약 1000 자 정도 예약사용.
    private String content;
    private String contentLength;
    private String writer;
    private String imageName; // 실제로 등록된 이미지 이름 문자열
    private Boolean isNotice;
    private Category category;
    

}
