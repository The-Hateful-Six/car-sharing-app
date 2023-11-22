package thehatefulsix.carsharingapp.util;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.user.User;

@Component
public class EmailNotificationSender {
    @Value("${email_password}")
    private String emailPassword;
    private final String email = "hatefulcarsharing@ukr.net";
    private final Session session = Session.getInstance(properties(), authenticator());

    private Properties properties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.ukr.net");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        return props;
    }

    private Authenticator authenticator() {
        return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, emailPassword);
            }
        };
    }

    public void sendRegistrationNotification(User user) {
        String subject = "Реєстрація в The Hateful Six: Car sharing";
        String message = "Вітаємо з реєстрацією на сайті оренди автомобілів!"
                         + System.lineSeparator()
                         + "Ваш пароль: " + user.getPassword();
        EmailUtil.sendEmail(session, user.getEmail(), subject, message);
    }

    public void sendCreateRentalNotification(Rental rental, User user, Car car) {
        String subject = "Нова оренда авто";
        String message = "Ви орендували автомобіль: " + System.lineSeparator()
                         + car.getBrand() + System.lineSeparator()
                         + car.getModel() + System.lineSeparator()
                         + car.getCarType() + System.lineSeparator()
                         + "Дата оренди: " + rental.getRentalDate() + System.lineSeparator()
                         + "Дата повернення авто: " + rental.getReturnDate()
                         + System.lineSeparator()
                         + "Вартість оренди на добу: " + car.getDailyFee() + System.lineSeparator()
                         + "Вдалої поїздки!";
        EmailUtil.sendEmail(session, user.getEmail(), subject, message);
    }
}
