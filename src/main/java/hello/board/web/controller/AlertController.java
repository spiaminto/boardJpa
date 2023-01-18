package hello.board.web.controller;

import hello.board.web.RedirectDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@Slf4j
public class AlertController {

    @GetMapping("/alert")
    public String getAlert(@ModelAttribute RedirectDTO redirectDTO) {

        log.info("alertController queryString = {}", redirectDTO.getQueryString());

        if (redirectDTO == null || redirectDTO.getMessage() == null) {
            redirectDTO.setMessage("alert 요청 오류");
            redirectDTO.setRedirectURL("/board/list");
        }
        return "/alert";
    }

}
