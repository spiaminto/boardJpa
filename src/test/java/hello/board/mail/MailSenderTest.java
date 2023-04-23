package hello.board.mail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MailSenderTest {

    @Autowired
    EmailSender mailSender;

    @Test
    void sendGmail() {
        EmailDTO mail = EmailDTO.builder().to("felix6265@gmail.com")
                .subject("테스트 제목").content("테스트 내용").build();

        mailSender.sendGmail(mail);


    }

}