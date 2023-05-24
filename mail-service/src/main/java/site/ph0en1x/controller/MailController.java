package site.ph0en1x.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.ph0en1x.dto.MailParams;
import site.ph0en1x.service.MailSenderService;

@RequestMapping("/mail")
@RestController
public class MailController {
    private final MailSenderService mailSender;

    public MailController(MailSenderService mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams) {
        mailSender.send(mailParams);
        return ResponseEntity.ok("Всё отправляется");
    }
}
