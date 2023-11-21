package thehatefulsix.carsharingapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
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
    public List<PaymentDto> getAllPaymentsByUserId(@PathVariable Long userId) {
        return paymentService.getAllPayments(userId);
    }

    @GetMapping("/success")
    public PaymentDto getSuccessfulResponse() {
        return null;
    }

    @GetMapping("/cancel")
    public String getCancelResponse() {
        return null;
    }
}
