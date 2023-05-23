package site.ph0en1x.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import site.ph0en1x.dto.MailParams;
import site.ph0en1x.service.MailSenderService;

@Service
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;


    public MailSenderServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    @Override
    public void send(MailParams mailParams) {
        var subject = "Ативация учетной записи в Бабушкином Погреботе";
        var messageBody = getActivationMailBody(mailParams.getId());
        var emailTo = mailParams.getEmailTo();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setSubject(subject);
        mailMessage.setTo(emailTo);
        mailMessage.setText(messageBody);

        javaMailSender.send(mailMessage);
    }

    private String getActivationMailBody(String id) {
        var msg = String.format("Для завершения регистрации перейдите по ссылке: \n%s",
                activationServiceUri);
        return msg.replace("{id}", id);
    }
}
