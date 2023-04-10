package hello.board.form;

import hello.board.domain.enums.Category;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BoardSaveForm {

    @NotBlank
    @Size(max = 30)
    private String title;
    @NotBlank
    // 이미지5장 기준 약 900~1000 자 정도 예약사용.
    @Size(max = 2200, message = "Content 제한량 초과")
    private String content;

    private String contentLength;

    private String writer;

    // DB 에 진짜로 저장할 이미지url
    private String imageName;

    private Category category;

}
