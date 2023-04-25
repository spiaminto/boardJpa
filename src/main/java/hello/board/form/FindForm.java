package hello.board.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class FindForm {
    @Email
    private String email;
    @NotBlank
    private String emailVerified;
    @NotBlank
    private String findOption;

    private String password;
}
