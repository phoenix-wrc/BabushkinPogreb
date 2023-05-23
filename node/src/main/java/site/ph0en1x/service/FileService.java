package site.ph0en1x.service;


import site.ph0en1x.entity.AppDocument;
import site.ph0en1x.entity.AppPhoto;
import site.ph0en1x.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}
