package site.ph0en1x.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessage(Update update);
    void processDocMessage(Update update);
    void processPhotoMessage(Update update);
}
