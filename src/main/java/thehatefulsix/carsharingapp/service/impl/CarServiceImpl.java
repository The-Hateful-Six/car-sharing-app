package thehatefulsix.carsharingapp.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.car.CarDto;
import thehatefulsix.carsharingapp.dto.car.CreateCarRequestDto;
import thehatefulsix.carsharingapp.exception.EntityNotFoundException;
import thehatefulsix.carsharingapp.mapper.CarMapper;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.service.CarService;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto createCarRequestDto) {
        Car car = carMapper.toCar(createCarRequestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public CarDto getCarById(Long id) {
        return carMapper.toDto(carRepository.getCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can`t find car by id " + id)));
    }

    @Override
    public CarDto update(CreateCarRequestDto createCarRequestDto, Long id) {
        Car car = carRepository.getCarById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find car by id " + id)
        );
        carMapper.updateCar(createCarRequestDto, car);
        car.setId(id);
        return carMapper.toDto(carRepository.save(car));
    }
}
