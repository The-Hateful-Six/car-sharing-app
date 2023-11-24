package thehatefulsix.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.concurrent.ExecutorService;
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
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.payment.Payment;
import thehatefulsix.carsharingapp.model.rental.Rental;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;
import thehatefulsix.carsharingapp.service.TelegramBotService;

@RequiredArgsConstructor
@Service
public class TelegramBotServiceImpl extends TelegramLongPollingBot implements TelegramBotService {
    private final TelegramBotConfig config;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final ExecutorService executorService;
    private final String chatId = "-4085484353L";

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
        executorService.execute(() -> {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new TelegramBotException("Something went wrong with executing message.");
            }
        });
    }

    private void prepareAndSendMessage(String chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message);
    }

    @Transactional
    public void sendMessage(String text) {
        prepareAndSendMessage(chatId, text);
    }

    @Override
    public void sendMessageToCertainGroup(String chatId, String text) {
        prepareAndSendMessage(chatId, text);
    }

    @Override
    public void sendMessageWithPhotoToGroup(String chatId, String text, String photoUrl) {
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

    @Transactional
    public void sendPaymentMessage(Payment payment) {
        Long rentalId = payment.getRentalId();
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(()
                -> new EntityNotFoundException("Can't find rental by id: " + rentalId));
        Long carId = rental.getCarId();
        Car car = carRepository.getCarById(carId).orElseThrow(()
                -> new EntityNotFoundException("Can't find car by id: " + carId));
        User user = userRepository.getReferenceById(rental.getUserId());
        String text = ("""
                New payment paid!
                Rental with id: %d for user %s %s
                Car: %s %s %s
                Total price paid: $%.2f
                Have a nice day!
                """.formatted(
                rentalId,
                user.getFirstName(),
                user.getLastName(),
                car.getBrand(),
                car.getModel(),
                car.getCarType(),
                payment.getAmountToPay()
        ));
        prepareAndSendMessage(chatId, text);
    }
}
