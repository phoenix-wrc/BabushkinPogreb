package site.ph0en1x.service;

import dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
