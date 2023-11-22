package thehatefulsix.carsharingapp.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface ScheduleService {
    void sendMessageMinutely();
}
