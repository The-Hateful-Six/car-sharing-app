package thehatefulsix.carsharingapp.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Data
@EnableScheduling
@Configuration
@PropertySource("application.properties")
public class TelegramBotConfig {
    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String botToken;
    //@Value("${bot.owner}")
    //Long botOwnerId;
}
