package hello.board.mail;

import lombok.*;

@ToString @Getter
@Builder
public class EmailDTO {

    private final String from = "spiaminto@gmail.com";    // 보내는 계정 (관리자)
    private String to;      // 받는 계정 (회원)
    private String subject;
    private String content;

}
