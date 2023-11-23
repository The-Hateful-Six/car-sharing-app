package thehatefulsix.carsharingapp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.stripe.model.tax.Registration;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.mapper.RentalMapper;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.car.CarType;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;
import thehatefulsix.carsharingapp.service.impl.RentalServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTests {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getRentalById_WithValidId_ReturnsValidRental() {
        Rental rental = new Rental();
        Long rentalId = 1L;
        rental.setId(rentalId);
        rental.setRentalDate(LocalDate.of(2023, 11, 20));
        rental.setReturnDate(LocalDate.of(2023, 11, 21));
        rental.setCarId(1L);
        rental.setUserId(1L);
        rental.setIsActive(true);
        rental.setIsDeleted(false);

        RentalDto expected = new RentalDto(
                1L,
                LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 21),
                null,
                1L,
                1L,
                true);

        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(expected);
        RentalDto actual = rentalService.getRentalById(rentalId);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getRentalById_WithNonExistingId_ReturnsValidRental() {
        Long userId = 100L;
        when(rentalRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.getRentalById(userId)
        );

        String expected = "Can`t find rental by id " + userId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    public void getAllByUserIdAndIsActive_WithExistingUserIdAndIsActive_ReturnsRentalDtos() {
        Long userId = 1L;
        boolean isActive = true;
        PageRequest pageRequest = PageRequest.of(0, 10);

        Rental rental1 = new Rental();
        rental1.setId(userId);
        rental1.setRentalDate(LocalDate.of(2023, 11, 20));
        rental1.setReturnDate(LocalDate.of(2023, 11, 21));
        rental1.setUserId(userId);
        rental1.setCarId(1L);
        rental1.setIsActive(isActive);
        rental1.setIsDeleted(false);

        RentalDto rentalDto1 = new RentalDto(
                1L,
                LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 21),
                null,
                1L,
                1L,
                true);

        List<RentalDto> expected = List.of(rentalDto1);

        when(rentalRepository.getAllByUserIdAndIsActive(userId, isActive, pageRequest))
                .thenReturn(List.of(rental1));
        when(rentalMapper.toDto(rental1)).thenReturn(rentalDto1);

        List<RentalDto> actual = rentalService.getAllByUserIdAndIsActive(userId, isActive, pageRequest);
        Assertions.assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            RentalDto expectedDto = expected.get(i);
            RentalDto actualDto = actual.get(i);
            Assertions.assertEquals(expectedDto, actualDto);
        }
    }

    @Test
    public void getAllByUserIdAndIsActive_WithNotExistingUser_ReturnsEmptyList() {
        Long userId = 100L;
        boolean isActive = true;
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(rentalRepository.getAllByUserIdAndIsActive(userId, isActive, pageRequest))
                .thenReturn(Collections.emptyList());

        List<RentalDto> actual = rentalService.getAllByUserIdAndIsActive(userId, isActive, pageRequest);

        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllByUserIdAndIsActive_WithNotValidIsActive_ReturnsEmptyList() {
        Long userId = 1L;
        boolean isActive = false;
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<RentalDto> actual = rentalService.getAllByUserIdAndIsActive(userId, isActive, pageRequest);
        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    public void createNewRental_WithValidRequest_ReturnsRentalDto() {
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto(LocalDate.of(2023,
                11, 19), LocalDate.of(2023, 11, 20), 1L);
        RentalDto rentalDto = new RentalDto(1L, LocalDate.of(2023,
                11, 19), LocalDate.of(2023, 11, 20), null, 1L, 1L, true);
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.of(2023, 11, 19));
        rental.setReturnDate(LocalDate.of(2023, 11, 20));
        rental.setUserId(1L);
        rental.setCarId(1L);
        rental.setIsActive(true);
        rental.setIsDeleted(false);

        Car car = new Car();
        Long carId = 1L;
        car.setId(carId);
        car.setCarType(CarType.SEDAN);
        car.setBrand("Audi");
        car.setModel("A 7");
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(100));
        car.setDeleted(false);

        String email = "test@example.com";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("12345678");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(rentalMapper.toRental(requestDto)).thenReturn(rental);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        int expectedInventory = 4;
        RentalDto actual = rentalService.save(requestDto);
        int actualInventory = car.getInventory();

        Assertions.assertEquals(expectedInventory, actualInventory);
        Assertions.assertEquals(rentalDto, actual);
    }

    @Test
    public void addActualReturnTime_ValidRequest_ReturnsRentalDto() {
        RentalDto rentalDto = new RentalDto(1L, LocalDate.of(2023,
                11, 19), LocalDate.of(2023, 11, 20), null, 1L, 1L, true);
        Rental rental = new Rental();
        Long rentalId = 1L;
        rental.setId(rentalId);
        rental.setRentalDate(LocalDate.of(2023, 11, 19));
        rental.setReturnDate(LocalDate.of(2023, 11, 20));
        rental.setUserId(1L);
        rental.setCarId(1L);
        rental.setIsActive(true);
        rental.setIsDeleted(false);

        Car car = new Car();
        Long carId = 1L;
        car.setId(carId);
        car.setCarType(CarType.SEDAN);
        car.setBrand("Audi");
        car.setModel("A 7");
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(100));
        car.setDeleted(false);

        String email = "test@example.com";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("12345678");

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(car)).thenReturn(car);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(rentalDto);

        int expectedInventory = 6;
        RentalDto actual = rentalService.addActualReturnTime(rentalId);
        int actualInventory = car.getInventory();

        Assertions.assertEquals(expectedInventory, actualInventory);
        Assertions.assertEquals(rentalDto, actual);
    }
}
