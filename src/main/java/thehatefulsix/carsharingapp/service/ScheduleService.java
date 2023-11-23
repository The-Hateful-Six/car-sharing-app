package thehatefulsix.carsharingapp.service;

public interface ScheduleService {
    void sendMessageMinutely();

    void sendMessageHourly();

    void checkSessionExpirationMinutely();
}
