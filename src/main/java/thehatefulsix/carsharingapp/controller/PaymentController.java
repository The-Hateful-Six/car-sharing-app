package thehatefulsix.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentCanceledDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentWithoutUrlDto;
import thehatefulsix.carsharingapp.service.PaymentService;

@Tag(name = "Payment management",
        description = "Endpoints for managing payments")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Create payment session",
            description = "Create payment session by rental id")
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public PaymentDto createPaymentSession(
            @RequestBody @Valid CreatePaymentRequestDto paymentRequestDto) {
        return paymentService.createPaymentSession(paymentRequestDto);
    }

    @Operation(summary = "Get payments",
            description = "Get all payments by client's id")
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping()
    public List<PaymentWithoutUrlDto> getAllPaymentsByUserId(
            @ParameterObject @PageableDefault(size = 5) Pageable pageable,
            @RequestParam @Positive Long userId) {
        return paymentService.getAllPayments(userId, pageable);
    }

    @Operation(summary = "Check successful stripe payments",
            description = "Check successful stripe payments (Endpoint for stripe redirection)")
    @GetMapping("/success")
    public PaymentWithoutUrlDto getSuccessfulResponse(
            @RequestParam("session_id") String sessionId) {
        return paymentService.getSuccessPayment(sessionId);
    }

    @Operation(summary = "Return payment paused message",
            description = "Return payment paused message (Endpoint for stripe redirection)")
    @GetMapping("/cancel")
    public PaymentCanceledDto getCancelResponse(@RequestParam("session_id") String sessionId) {
        return paymentService.getCancelResponse(sessionId);
    }
}
