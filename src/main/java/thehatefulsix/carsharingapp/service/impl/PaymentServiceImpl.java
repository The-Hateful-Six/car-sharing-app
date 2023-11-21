package thehatefulsix.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
import thehatefulsix.carsharingapp.exception.PaymentStripeException;
import thehatefulsix.carsharingapp.mapper.PaymentMapper;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.payment.Payment;
import thehatefulsix.carsharingapp.model.payment.PaymentStatus;
import thehatefulsix.carsharingapp.model.payment.PaymentType;
import thehatefulsix.carsharingapp.payment.strategy.OperationHandler;
import thehatefulsix.carsharingapp.payment.strategy.OperationStrategy;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.repository.PaymentRepository;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.service.PaymentService;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String SUCCESS_URL = "http://localhost:8080/payments/success";
    private static final String CANCEL_URL = "http://localhost:8080/payments/cancel";
    @Value("${stripe.secret.key}")
    private String stripeKey;

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OperationStrategy operationStrategy;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

    public void addSuccessPayment(String sessionId) {
        Session session = new Session();
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new PaymentStripeException("Can't find payment session");
        }
    }

    @Override
    public PaymentDto createPaymentSession(CreatePaymentRequestDto createPaymentDto) {
        Session session;
        SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(getTotalPrice(createPaymentDto).longValue())
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Payment")
                                        .setDescription("any description")
                                        .build())
                                .build()
                        ).setQuantity(1L)
                        .build()
                ).setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(CANCEL_URL);
        try {
            session = Session.create(builder.build());
        } catch (StripeException e) {
            throw new PaymentStripeException("Can't create payment session", e);
        }

        return paymentMapper.toDto(createPayment(createPaymentDto,session));
    }

    @Override
    public List<PaymentDto> getAllPayments(Long userId, Pageable pageable) {
        return paymentRepository.findPaymentsByUserId(userId, pageable).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    private Payment createPayment(CreatePaymentRequestDto createPaymentDto, Session session) {
        Payment payment = new Payment();
        payment.setAmountToPay(getTotalPrice(createPaymentDto));
        payment.setType(PaymentType.valueOf(createPaymentDto.paymentType()));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setRentalId(createPaymentDto.rentalId());
        return paymentRepository.save(payment);
    }

    private BigDecimal getTotalPrice(CreatePaymentRequestDto createPaymentDto) {
        Rental rental = rentalRepository.findById(createPaymentDto.rentalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find rental with id: " + createPaymentDto.rentalId()));
        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find car with id: " + rental.getCarId()));
        String string = createPaymentDto.paymentType();
        OperationHandler operationHandler = operationStrategy.get(PaymentType.valueOf(string));
        return operationHandler.getTotalPrice(rental, car);
    }

    private SessionCreateParams.Builder getSessionBuilder() {
        SessionCreateParams.Builder sessionBuilder = new SessionCreateParams.Builder();
        sessionBuilder.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD);
        sessionBuilder.setMode(SessionCreateParams.Mode.PAYMENT);
        sessionBuilder.setSuccessUrl(SUCCESS_URL);
        sessionBuilder.setCancelUrl(CANCEL_URL);
        sessionBuilder.setExpiresAt(3600L);
        return sessionBuilder;
    }
}
