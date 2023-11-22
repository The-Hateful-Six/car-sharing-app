package thehatefulsix.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.car.CarDto;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.mapper.RentalMapper;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.repository.RentalRepository;
import thehatefulsix.carsharingapp.service.CarService;
import thehatefulsix.carsharingapp.service.RentalService;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarService carService;
    private final CarRepository carRepository;

    @Override
    public RentalDto save(CreateRentalRequestDto requestDto) {
        Long carId = requestDto.carId();
        Car car = carRepository.findById(carId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find rental by id " + carId));
        car.setInventory(car.getInventory() - 1);
        Rental rental = rentalMapper.toRental(requestDto);
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
        Car car = carRepository.findById(carId).orElseThrow( ()
                -> new EntityNotFoundException("Can`t find car by id"));
        car.setInventory(car.getInventory() + 1);
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(()
                -> new EntityNotFoundException("Can`t find rental by id " + rentalId));
        rental.setActualReturnDate(LocalDate.now());
        rental.setIsActive(false);
        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }
}
