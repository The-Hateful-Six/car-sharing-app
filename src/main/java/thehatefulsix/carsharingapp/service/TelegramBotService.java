package thehatefulsix.carsharingapp.service;

public interface TelegramBotService {
    void sendMessage(String text);

    void sendMessageToCertainGroup(Long chatId, String text);

    void sendMessageWithPhotoToGroup(Long chatId, String text, String photoUrl);
}
