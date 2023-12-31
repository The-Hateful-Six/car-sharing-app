package thehatefulsix.carsharingapp.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import thehatefulsix.carsharingapp.exception.TelegramBotException;
import thehatefulsix.carsharingapp.service.impl.TelegramBotServiceImpl;

@Data
@Component
public class TelegramBotInitializer {
    @Autowired
    private TelegramBotServiceImpl bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            throw new TelegramBotException("Something went wrong "
                    + "with initializing a telegram bot." + e);
        }
    }
}
