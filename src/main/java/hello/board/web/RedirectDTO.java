package hello.board.web;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// redirect 메시지와 url 을 전송할 객체
public class RedirectDTO {
    private String redirectURL;
    private String message;
    private String queryString;

    public RedirectDTO(String redirectURL, String message) {
        this.redirectURL = redirectURL;
        this.message = message;
    }
}
