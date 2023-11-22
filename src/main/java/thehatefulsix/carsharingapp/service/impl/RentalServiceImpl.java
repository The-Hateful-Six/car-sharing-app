package thehatefulsix.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.mapper.RentalMapper;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;
import thehatefulsix.carsharingapp.service.RentalService;
import thehatefulsix.carsharingapp.service.TelegramBotService;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final TelegramBotService telegramBotService;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public RentalDto save(CreateRentalRequestDto requestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long carId = requestDto.carId();
        Car car = carRepository.findById(carId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find rental by id " + carId));
        car.setInventory(car.getInventory() - 1);
        Rental rental = rentalMapper.toRental(requestDto);
        rental.setUserId(userRepository.findByEmail(email).get().getId());
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
        Long carId = getRentalById(rentalId).carId();
        Car car = carRepository.findById(carId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find car by id"));
        car.setInventory(car.getInventory() + 1);
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find rental by id " + rentalId));
        rental.setActualReturnDate(LocalDate.now());
        rental.setIsActive(false);
        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Transactional
    public void sendNotificationAboutRentDelay() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<User> users = rentalRepository.findAll().stream()
                .filter(r -> r.getReturnDate().isBefore(yesterday) && r.getActualReturnDate() == null)
                .map(Rental::getUserId)
                .distinct()
                .map(userId -> userRepository.findById(userId).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        String usersEmail = users.stream()
                        .map(User::getEmail)
                                .collect(Collectors.joining("\n"));
        telegramBotService.sendMessage("Users who zcomunizdiv cars: \n" + usersEmail);
    }
}
