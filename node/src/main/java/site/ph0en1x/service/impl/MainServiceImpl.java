package site.ph0en1x.service.impl;

import lombok.extern.log4j.Log4j;
import site.ph0en1x.dao.AppUserDAO;
import site.ph0en1x.dao.RawDataDAO;
import site.ph0en1x.entity.AppDocument;
import site.ph0en1x.entity.AppPhoto;
import site.ph0en1x.entity.AppUser;
import site.ph0en1x.entity.RawData;
import site.ph0en1x.exception.UploadFileException;
import site.ph0en1x.service.AppUserService;
import site.ph0en1x.service.FileService;
import site.ph0en1x.service.MainService;
import site.ph0en1x.service.ProducerService;
import site.ph0en1x.service.enums.LinkType;
import site.ph0en1x.service.enums.ServiceCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static site.ph0en1x.entity.enums.UserState.BASIC_STATE;
import static site.ph0en1x.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService,
                           AppUserDAO appUserDAO, FileService fileService, AppUserService appUserService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "!!!";

        var serviceCommand = ServiceCommands.fromValue(text);
        if (ServiceCommands.CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if(BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if(WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Неизвестный стейт: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);

            var answer = "Документ успешно загружен! Ссылка на скачивание: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "К сожалению, загрузка не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppPhoto  photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);

            var answer = "Фото успешно загружен! Ссылка на скачивание: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "К сожалению, загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String text) {
        var cmd = ServiceCommands.fromValue(text);
        if(ServiceCommands.REGISTRATION.equals(cmd)) {
            return appUserService.registerUser(appUser);
        } else if (ServiceCommands.HELP.equals(cmd)) {
            return help();
        } else if (ServiceCommands.START.equals(cmd)) {
            return "Привествую)) Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда. Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return """
                Список доступных команд:\s
                /cancel - отмена выполнения текущей команды.\s
                /registration - регистрация в сервисе.
                """;
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена))";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        var persistentAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if(persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            log.debug("User " + telegramUser.getId() + " is not find. Create new one");
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser.orElseThrow();
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
