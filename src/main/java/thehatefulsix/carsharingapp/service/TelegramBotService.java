package thehatefulsix.carsharingapp.service;

import thehatefulsix.carsharingapp.model.payment.Payment;

public interface TelegramBotService {
    void sendMessage(String text);

    void sendPaymentMessage(Payment payment);
}
