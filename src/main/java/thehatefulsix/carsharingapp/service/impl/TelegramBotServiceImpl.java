package thehatefulsix.carsharingapp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import thehatefulsix.carsharingapp.config.TelegramBotConfig;
import thehatefulsix.carsharingapp.exception.TelegramBotException;
import thehatefulsix.carsharingapp.service.TelegramBotService;

@RequiredArgsConstructor
@Service
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {
    private final TelegramBotConfig config;
    private final Long chatId = -4085484353L;

    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramBotException("Something went wrong with executing message.");
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    @Transactional
    public void sendMessage(String text) {
        prepareAndSendMessage(chatId, text);
    }

    @Override
    public void sendMessageToCertainGroup(Long chatId, String text) {
        prepareAndSendMessage(chatId, text);
    }

    @Override
    public void sendMessageWithPhotoToGroup(Long chatId, String text, String photoUrl) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(photoUrl));
        if (text != null) {
            photo.setCaption(text);
        }
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            throw new TelegramBotException("Can't send message with photo.");
        }
    }
}
