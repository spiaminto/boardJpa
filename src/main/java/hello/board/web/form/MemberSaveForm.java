package hello.board.web.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MemberSaveForm {

    @NotBlank
    @Size(max = 16)
    private String loginId;

    @NotBlank
    @Size(max = 16)
    private String username;

//    size 최소값 주어짐.
//    @NotBlank
    @Size(min = 4, max = 16)
    private String password;

    @Size(max = 30)
    @Email
    private String email;
}
