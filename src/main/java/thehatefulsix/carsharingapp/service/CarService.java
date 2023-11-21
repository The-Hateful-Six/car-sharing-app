package thehatefulsix.carsharingapp.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import thehatefulsix.carsharingapp.dto.car.CarDto;
import thehatefulsix.carsharingapp.dto.car.CreateCarRequestDto;

public interface CarService {
    CarDto save(CreateCarRequestDto createCarRequestDto);

    List<CarDto> findAll(Pageable pageable);

    void deleteById(Long id);

    CarDto getCarById(Long id);

    CarDto update(CreateCarRequestDto createCarRequestDto, Long id);
}
