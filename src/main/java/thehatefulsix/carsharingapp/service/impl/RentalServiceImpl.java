package thehatefulsix.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.mapper.RentalMapper;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.service.RentalService;
import thehatefulsix.carsharingapp.service.TelegramBotService;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final TelegramBotService telegramBotService;

    @Override
    public RentalDto save(CreateRentalRequestDto createRentalRequestDto) {
        Rental rental = rentalMapper.toRental(createRentalRequestDto);
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
    public RentalDto addActualReturnTime(Long rentalId, LocalDate actualReturnDate) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find rental by id " + rentalId));
        rental.setActualReturnDate(actualReturnDate);
        rental.setActive(false);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    public void sendNotificationAboutRentDelay() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<User> users = rentalRepository.findUsersWithUnreturnedCars(yesterday);
        String usersEmails = users.stream()
                        .map(User::getEmail)
                                .collect(Collectors.joining("\n"));
        telegramBotService.sendMessage("Users, that don't return cars: \n"
                        + usersEmails);
    }
}
