package hello.board.web.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MemberEditForm {
    private Long id;

    @NotBlank
    @Size(max = 8)
    private String loginId;

    @NotBlank
    @Size(max = 8)
    private String username;

    @Size(min = 4, max = 12)
    private String password;


}
