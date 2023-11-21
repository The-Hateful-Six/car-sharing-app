package thehatefulsix.carsharingapp.service.telegram;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import thehatefulsix.carsharingapp.config.TelegramBotConfig;
import thehatefulsix.carsharingapp.exception.TelegramBotException;
import thehatefulsix.carsharingapp.model.User;
import thehatefulsix.carsharingapp.repository.UserRepository;
import java.util.List;

@Service
public class TelegramBotService extends TelegramLongPollingBot {
    @Autowired
    private final UserRepository userRepository;
    private final Long chatId = -4085484353L;
    private final String MESSAGE = "A new user authorized with this email: ";
    final TelegramBotConfig config;
    public TelegramBotService(UserRepository userRepository, TelegramBotConfig config) {
        this.userRepository = userRepository;
        this.config = config;
    }

    @Override
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

    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramBotException("Something went wrong with executing message.");
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void sendMessageAutomatically() {
        List<User> users = userRepository.findAll();
        users.stream()
                .filter(u -> !u.isMentioned())
                .forEach(u -> {
                    prepareAndSendMessage(chatId, MESSAGE + u.getEmail());
                    u.setMentioned(true);
                });
    }
}
