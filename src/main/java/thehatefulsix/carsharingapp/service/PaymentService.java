package thehatefulsix.carsharingapp.service;

import java.util.List;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;

public interface PaymentService {

    PaymentDto createPaymentSession(CreatePaymentRequestDto createPaymentDto);

    List<PaymentDto> getAllPayments(Long userId);
}
