package thehatefulsix.carsharingapp.service;

public interface TelegramBotService {
    void sendMessage(String text);

    void sendMessageToCertainGroup(String channelUsername, String text);

    void sendMessageWithPhotoToGroup(String chatId, String text, String photoUrl);
}
