package thehatefulsix.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.exception.RentalException;
import thehatefulsix.carsharingapp.mapper.RentalMapper;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.payment.Payment;
import thehatefulsix.carsharingapp.model.payment.PaymentStatus;
import thehatefulsix.carsharingapp.model.rental.Rental;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.repository.PaymentRepository;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;
import thehatefulsix.carsharingapp.service.RentalService;
import thehatefulsix.carsharingapp.service.TelegramBotService;
import thehatefulsix.carsharingapp.util.EmailNotificationSender;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final TelegramBotService telegramBotService;
    private final EmailNotificationSender emailNotificationSender;
    private final PaymentRepository paymentRepository;

    @Override
    public RentalDto save(CreateRentalRequestDto requestDto) {
        User user = getUserWithoutDebt();
        Car car = getAvailableCar(requestDto);
        car.setInventory(car.getInventory() - 1);
        Rental rental = rentalMapper.toRental(requestDto);
        rental.setUserId(user.getId());
        emailNotificationSender.sendCreateRentalNotification(rental, user, car);
        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalDto> getAllByUserIdAndIsActive(Long userId,
                                                     boolean isActive,
                                                     Pageable pageable) {
        return rentalRepository.getAllByUserIdAndIsActive(userId, isActive, pageable).stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalDto getRentalById(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find rental by id " + rentalId));
        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalDto addActualReturnTime(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find rental by id " + rentalId));
        if (!rental.getIsActive()) {
            throw new RentalException("Can't return rental with id: " + rentalId);
        }
        Long carId = getRentalById(rentalId).carId();
        Car car = carRepository.findById(carId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find car by id"));
        car.setInventory(car.getInventory() + 1);
        rental.setActualReturnDate(LocalDate.now());
        rental.setIsActive(false);
        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Transactional
    public void sendNotificationAboutRentDelay() {
        List<User> users = rentalRepository.findAll().stream()
                .filter(r -> r.getReturnDate().isBefore(LocalDate.now())
                             && r.getActualReturnDate() == null)
                .map(Rental::getUserId)
                .distinct()
                .map(userId -> userRepository.findById(userId).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        if (users.isEmpty()) {
            telegramBotService.sendMessage("No rentals overdue today!");
            return;
        }
        String usersEmail = users.stream()
                .map(User::getEmail)
                .collect(Collectors.joining("\n"));
        telegramBotService.sendMessage("Customers who did not return the cars "
                                       + "before the return date: \n" + usersEmail);
    }

    private User getUserWithoutDebt() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("Can`t find user by email " + email));
        if (paymentRepository.findPaymentsByUserId(user.getId()).stream()
                .map(Payment::getStatus)
                .toList()
                .contains(PaymentStatus.PENDING)) {
            throw new RentalException("Can't create rental for user with id: " + user.getId());
        }
        return user;
    }

    private Car getAvailableCar(CreateRentalRequestDto requestDto) {
        Long carId = requestDto.carId();
        Car car = carRepository.findById(carId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find rental by id " + carId));
        if (car.getInventory() <= 0) {
            throw new RentalException("Can't create rental for car with id: " + carId);
        }
        return car;
    }
}
