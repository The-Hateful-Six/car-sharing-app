package thehatefulsix.carsharingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.service.AdvertService;
import thehatefulsix.carsharingapp.service.PaymentService;
import thehatefulsix.carsharingapp.service.RentalService;
import thehatefulsix.carsharingapp.service.ScheduleService;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final RentalService rentalService;
    private final PaymentService paymentService;
    private final AdvertService advertService;

    @Scheduled(cron = "0 * * * * *")
    @Override
    public void sendMessageMinutely() {
        rentalService.sendNotificationAboutRentDelay();
    }

    @Scheduled(cron = "0 * * * * *")
    @Override
    public void checkSessionExpirationMinutely() {
        paymentService.checkSessionExpiration();
    }

    @Scheduled(cron = "0 * * * * *")
    @Override
    public void sendMessageHourly() {
        advertService.sentAdvert();
    }
}
