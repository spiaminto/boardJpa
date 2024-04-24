package hello.board.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class EmailSender {
    private final JavaMailSender javaMailSender; // autowire 못하는거 버그인듯

    /**
     * Google smtp 서버를 이용하여 G메일을 보냄 (비동기)
     */
    @Async
    public void sendGmail(EmailDTO emailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDTO.getTo());
        message.setFrom(emailDTO.getFrom());
        message.setSubject(emailDTO.getSubject());
        message.setText(emailDTO.getContent());
        log.info("sendGmail(), mailDTO={}", emailDTO);
        javaMailSender.send(message);
    }


}
