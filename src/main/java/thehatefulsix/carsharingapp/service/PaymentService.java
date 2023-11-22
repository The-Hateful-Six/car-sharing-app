package thehatefulsix.carsharingapp.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentCanceledDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentWithoutUrlDto;

public interface PaymentService {

    PaymentDto createPaymentSession(CreatePaymentRequestDto createPaymentDto);

    List<PaymentWithoutUrlDto> getAllPayments(Long userId, Pageable pageable);

    PaymentWithoutUrlDto getSuccessPayment(String sessionId);

    PaymentCanceledDto getCancelResponse(String sessionId);
}
