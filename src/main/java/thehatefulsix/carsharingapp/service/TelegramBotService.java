package thehatefulsix.carsharingapp.service;

import thehatefulsix.carsharingapp.model.payment.Payment;

public interface TelegramBotService {
    void sendMessage(String text);

    void sendPaymentMessage(Payment payment);

    void sendMessageToCertainGroup(Long chatId, String text);

    void sendMessageWithPhotoToGroup(Long chatId, String text, String photoUrl);
}
