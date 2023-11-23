package thehatefulsix.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentCanceledDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentWithoutUrlDto;
import thehatefulsix.carsharingapp.exception.PaymentStripeException;
import thehatefulsix.carsharingapp.mapper.PaymentMapper;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.payment.Payment;
import thehatefulsix.carsharingapp.model.payment.PaymentStatus;
import thehatefulsix.carsharingapp.model.payment.PaymentType;
import thehatefulsix.carsharingapp.model.user.RoleName;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.payment.strategy.OperationHandler;
import thehatefulsix.carsharingapp.payment.strategy.OperationStrategy;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.repository.PaymentRepository;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;
import thehatefulsix.carsharingapp.service.PaymentService;
import thehatefulsix.carsharingapp.service.TelegramBotService;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String SUCCESS_URL = "http://localhost:8080/payments/success";
    private static final String CANCEL_URL = "http://localhost:8080/payments/cancel";
    private static final String PAYMENT_CURRENCY = "usd";
    private static final String PAYMENT_NAME = "Payment";
    private static final String SESSION_AUTO_REQUEST = "?session_id={CHECKOUT_SESSION_ID}";
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    private static final Long EXPIRATION_TIME_IN_SECONDS = 86000L;
    private static final Long SECOND_DIVIDE = 1000L;
    private static final Long DEFAULT_QUANTITY = 1L;
    private static final BigDecimal CENT_DIVIDE = new BigDecimal(100);

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OperationStrategy operationStrategy;
    private final TelegramBotService telegramBotService;
    private final UserRepository userRepository;

    @Value("${stripe.secret.key}")
    private String stripeKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

    @Override
    public PaymentDto createPaymentSession(CreatePaymentRequestDto createPaymentDto) {
        Session session;
        SessionCreateParams.Builder builder = getSessionBuilder(createPaymentDto);
        try {
            session = Session.create(builder.build());
        } catch (StripeException e) {
            throw new PaymentStripeException("Can't create payment session", e);
        }
        return paymentMapper.toDto(createPayment(createPaymentDto, session));
    }

    @Transactional
    @Override
    public PaymentWithoutUrlDto getSuccessPayment(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId);
        payment.setStatus(PaymentStatus.PAID);
        telegramBotService.sendPaymentMessage(payment);
        return paymentMapper.toWithoutUrlDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentCanceledDto getCancelResponse(String sessionId) {
        Session session;
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new PaymentStripeException(
                    "Can't find payment session with id: " + sessionId, e);
        }
        Instant instant = Instant.ofEpochMilli(session.getExpiresAt() * SECOND_DIVIDE);
        LocalDateTime expireTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        String formattedDateTime = expireTime
                .format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String message = "Something went wrong. You can perform the operation "
                         + "for 24 hours, until " + formattedDateTime;

        System.out.println(formattedDateTime);

        return new PaymentCanceledDto(message);
    }

    @Override
    public List<PaymentWithoutUrlDto> getAllPayments(Pageable pageable) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
                .contains(RoleName.MANAGER.name())
        ) {
            return paymentRepository.findAll(pageable).stream()
                    .map(paymentMapper::toWithoutUrlDto)
                    .toList();
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("Can't find user by email: " + email));
        return paymentRepository.findPaymentsByUserId(user.getId()).stream()
                .map(paymentMapper::toWithoutUrlDto)
                .toList();
    }

    @Override
    public void checkSessionExpiration() {
        List<String> expiredList = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus().equals(PaymentStatus.PENDING))
                .map(p -> {
                    try {
                        return Session.retrieve(p.getSessionId());
                    } catch (StripeException e) {
                        throw new PaymentStripeException(
                                "Can't find payment session with id: " + p.getSessionId(), e);
                    }
                })
                .filter(s -> s.getExpiresAt() <= System.currentTimeMillis() / SECOND_DIVIDE)
                .map(Session::getId)
                .toList();
        if (!expiredList.isEmpty()) {
            expiredList
                    .forEach(sessionId -> {
                        Payment payment = paymentRepository.findBySessionId(sessionId);
                        payment.setStatus(PaymentStatus.EXPIRED);
                        paymentRepository.save(payment);
                    });
        }
    }

    private Payment createPayment(CreatePaymentRequestDto createPaymentDto, Session session) {
        Payment payment = new Payment();
        payment.setAmountToPay(BigDecimal.valueOf(session.getAmountTotal())
                .divide(CENT_DIVIDE));
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
        Car car = carRepository.getCarById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find car with id: " + rental.getCarId()));
        String string = createPaymentDto.paymentType();
        OperationHandler operationHandler = operationStrategy.get(PaymentType.valueOf(string));
        return operationHandler.getTotalPrice(rental, car);
    }

    private SessionCreateParams.Builder getSessionBuilder(
            CreatePaymentRequestDto createPaymentDto) {
        return new SessionCreateParams.Builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(getPriceData(createPaymentDto))
                        .setQuantity(DEFAULT_QUANTITY)
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL + SESSION_AUTO_REQUEST)
                .setCancelUrl(CANCEL_URL + SESSION_AUTO_REQUEST)
                .setExpiresAt(getExpirationTime());
    }

    private SessionCreateParams.LineItem.PriceData.ProductData getProductData(
            CreatePaymentRequestDto paymentDto) {
        Rental rental = rentalRepository.findById(paymentDto.rentalId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find rental with id: " + paymentDto.rentalId()));
        Car car = carRepository.getCarById(rental.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find car with id: " + rental.getCarId()));
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("for renting ")
                .append(car.getBrand())
                .append(" ")
                .append(car.getModel());
        return SessionCreateParams.LineItem
                .PriceData.ProductData.builder()
                .setName(PAYMENT_NAME)
                .setDescription(descriptionBuilder.toString())
                .build();
    }

    private SessionCreateParams.LineItem.PriceData getPriceData(
            CreatePaymentRequestDto createPaymentDto) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(PAYMENT_CURRENCY)
                .setUnitAmount(getTotalPrice(createPaymentDto).longValue())
                .setProductData(getProductData(createPaymentDto))
                .build();
    }

    private long getExpirationTime() {
        return System.currentTimeMillis() / SECOND_DIVIDE + EXPIRATION_TIME_IN_SECONDS;
    }
}
