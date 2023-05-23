package site.ph0en1x.service;

import site.ph0en1x.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
