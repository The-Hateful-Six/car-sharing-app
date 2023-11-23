package thehatefulsix.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.stripe.Stripe;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentWithoutUrlDto;
import thehatefulsix.carsharingapp.mapper.PaymentMapper;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.car.CarType;
import thehatefulsix.carsharingapp.model.payment.Payment;
import thehatefulsix.carsharingapp.model.payment.PaymentStatus;
import thehatefulsix.carsharingapp.model.payment.PaymentType;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.payment.strategy.OperationStrategy;
import thehatefulsix.carsharingapp.payment.strategy.impl.PaymentOperation;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.repository.PaymentRepository;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;
import thehatefulsix.carsharingapp.service.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private OperationStrategy operationStrategy;
    @Mock
    private PaymentOperation paymentOperation;
    @Mock
    private TelegramBotService telegramBotService;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName(value = "Create new payment session")
    public void createPaymentSession_WithValidData_ShouldReturnPaymentDto() {
        Stripe.apiKey = "sk_test_51OEWGWH4cavW5x6AZWqwkDz2UY"
                      + "TvZCTHw2Xi2JgBehl077b2bb83SCEAoF6LU"
                      + "kGZqZS43UFn4iH3Oj45snsVfxAT00fL9Bm83K";
        Long id = 1L;

        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(
                1L,
                "PAYMENT"
        );

        Payment payment = new Payment();
        payment.setId(id);
        payment.setSessionId("random_test_id");
        payment.setSessionUrl("http://test.url/random");
        payment.setType(PaymentType.PAYMENT);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(180));
        payment.setRentalId(requestDto.rentalId());
        payment.setDeleted(false);

        Rental rental = new Rental();
        rental.setId(id);
        rental.setRentalDate(LocalDate.of(2023, 11, 21));
        rental.setReturnDate(LocalDate.of(2023, 11, 23));
        rental.setCarId(id);
        rental.setUserId(id);
        rental.setIsActive(true);
        rental.setIsDeleted(false);

        Car car = new Car();
        car.setId(id);
        car.setModel("X5");
        car.setBrand("BWM");
        car.setCarType(CarType.SUV);
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(90));
        car.setDeleted(false);

        PaymentDto paymentDto = new PaymentDto(
                payment.getId(),
                payment.getStatus().toString(),
                payment.getType().toString(),
                payment.getRentalId(),
                payment.getAmountToPay().intValue(),
                payment.getSessionUrl());

        BigDecimal expectedTotalPrice = BigDecimal.valueOf(180);

        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        when(rentalRepository.findById(id)).thenReturn(Optional.of(rental));
        when(carRepository.getCarById(id)).thenReturn(Optional.of(car));
        when(operationStrategy.get(PaymentType.PAYMENT)).thenReturn(paymentOperation);
        when(paymentOperation.getTotalPrice(rental, car)).thenReturn(expectedTotalPrice);

        PaymentDto actual = paymentService.createPaymentSession(requestDto);

        assertEquals(actual, paymentDto);
    }

    @Test
    @DisplayName(value = "Get list of all specific user payments")
    void getAllPayments_ValidUserId_ShouldReturnListOfPaymentWithoutUrlDto() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        Payment payment = new Payment();
        payment.setId(user.getId());
        payment.setSessionId("random_test_id");
        payment.setSessionUrl("http://test.url/random");
        payment.setType(PaymentType.PAYMENT);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(180));
        payment.setRentalId(1L);
        payment.setDeleted(false);

        PaymentWithoutUrlDto paymentWithoutUrlDto = new PaymentWithoutUrlDto(
                payment.getId(),
                payment.getStatus().toString(),
                payment.getType().toString(),
                payment.getRentalId(),
                payment.getAmountToPay().intValue()
        );

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Payment> paymentsList = List.of(payment);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(paymentRepository.findPaymentsByUserId(user.getId())).thenReturn(paymentsList);
        when(paymentMapper.toWithoutUrlDto(payment)).thenReturn(paymentWithoutUrlDto);

        List<PaymentWithoutUrlDto> actual = paymentService.getAllPayments(pageRequest);
        List<PaymentWithoutUrlDto> expected = List.of(paymentWithoutUrlDto);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName(value = "Get success payment")
    void getSuccessPayment_ValidSessionId_ShouldReturnPaymentWithoutUrlDto() {
        Long id = 1L;

        Payment payment = new Payment();
        payment.setId(id);
        payment.setSessionId("random_test_id");
        payment.setSessionUrl("http://test.url/random");
        payment.setType(PaymentType.PAYMENT);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(180));
        payment.setRentalId(id);
        payment.setDeleted(false);

        PaymentWithoutUrlDto expected = new PaymentWithoutUrlDto(
                payment.getId(),
                "PAID",
                payment.getType().toString(),
                payment.getRentalId(),
                payment.getAmountToPay().intValue()
        );

        String sessionId = "random_session_id";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toWithoutUrlDto(payment)).thenReturn(expected);

        PaymentWithoutUrlDto actual = paymentService.getSuccessPayment(sessionId);

        assertEquals(actual, expected);
    }
}
