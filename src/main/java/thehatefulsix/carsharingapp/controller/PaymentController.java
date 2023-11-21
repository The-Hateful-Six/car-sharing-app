package thehatefulsix.carsharingapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentCanceledDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentWithoutUrlDto;
import thehatefulsix.carsharingapp.service.PaymentService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPaymentSession(CreatePaymentRequestDto paymentRequestDto) {
        return paymentService.createPaymentSession(paymentRequestDto);
    }

    @GetMapping("/?user_id={userId}")
    public List<PaymentWithoutUrlDto> getAllPaymentsByUserId(@PathVariable Long userId) {
        return paymentService.getAllPayments(userId);
    }

    @GetMapping("/success")
    public PaymentWithoutUrlDto getSuccessfulResponse(
            @RequestParam("session_id") String sessionId) {
        return paymentService.getSuccessPayment(sessionId);
    }

    @GetMapping("/cancel")
    public PaymentCanceledDto getCancelResponse(@RequestParam("session_id") String sessionId) {
        return paymentService.getCancelResponse(sessionId);
    }
}
