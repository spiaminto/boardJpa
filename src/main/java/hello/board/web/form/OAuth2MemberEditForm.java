package hello.board.web.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class OAuth2MemberEditForm {

    @NotBlank
    @Size(max = 16)
    private String username;

    private String provider;

    private String providerId;
}
