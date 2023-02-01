package hello.board.web.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MemberEditForm {
    private Long id;

    @NotBlank
    @Size(max = 16)
    private String loginId;

    @NotBlank
    @Size(max = 16)
    private String username;

    @Size(min = 4, max = 16)
    private String password;

    @Size(max = 30)
    @Email
    private String email;

}
