package thehatefulsix.carsharingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
import thehatefulsix.carsharingapp.service.PaymentServiceImpl;

@SpringBootApplication
public class CarSharingAppApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(
                CarSharingAppApplication.class, args);
        PaymentServiceImpl paymentService = context.getBean(
                PaymentServiceImpl.class);
        PaymentDto payment = paymentService.createPaymentSession(
                new CreatePaymentRequestDto(1L, "PAYMENT"));
        System.out.println(payment);
    }

}
