package hello.board.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginForm {

    @NotBlank
    @Size(max = 16)
    private String loginId;

    @NotBlank
    @Size(min = 4, max = 16)
    private String password;

}
