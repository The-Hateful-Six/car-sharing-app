package thehatefulsix.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import thehatefulsix.carsharingapp.dto.car.CarDto;
import thehatefulsix.carsharingapp.dto.car.CreateCarRequestDto;
import thehatefulsix.carsharingapp.exception.EntityNotFoundException;
import thehatefulsix.carsharingapp.mapper.CarMapper;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.car.CarType;
import thehatefulsix.carsharingapp.repository.CarRepository;
import thehatefulsix.carsharingapp.service.impl.CarServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CarServiceTests {
    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Save valid car")
    void save_validCar_returnCarDto() {
        CreateCarRequestDto createCarRequestDto;
        createCarRequestDto = new CreateCarRequestDto(
                "Audi", "Q7",1, "HATCHBACK", BigDecimal.valueOf(200));
        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);
        car.setBrand("Audi");
        car.setModel("Q7");
        car.setInventory(1);
        car.setCarType(CarType.valueOf("HATCHBACK"));
        car.setDailyFee(BigDecimal.valueOf(200));

        CarDto carDto = new CarDto();
        carDto.setId(car.getId());
        carDto.setBrand(car.getBrand());
        carDto.setModel(car.getModel());
        carDto.setInventory(car.getInventory());
        carDto.setCarType(car.getCarType());
        carDto.setDailyFee(car.getDailyFee());

        Mockito.when(carMapper.toCar(createCarRequestDto)).thenReturn(car);
        Mockito.when(carRepository.save(car)).thenReturn(car);
        Mockito.when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actual = carService.save(createCarRequestDto);
        assertEquals(actual, carDto);
    }

    @Test
    @DisplayName("get car by valid id")
    public void getCarById_validCar_returnCarDto() {
        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);
        car.setBrand("Audi");
        car.setModel("Q7");
        car.setInventory(1);
        car.setCarType(CarType.valueOf("HATCHBACK"));
        car.setDailyFee(BigDecimal.valueOf(200));

        CarDto carDto = new CarDto();
        carDto.setId(car.getId());
        carDto.setBrand(car.getBrand());
        carDto.setModel(car.getModel());
        carDto.setInventory(car.getInventory());
        carDto.setCarType(car.getCarType());
        carDto.setDailyFee(car.getDailyFee());

        Mockito.when(carRepository.getCarById(carId)).thenReturn(Optional.of(car));
        Mockito.when(carMapper.toDto(car)).thenReturn(carDto);
        CarDto actual = carService.getCarById(carId);
        Assertions.assertEquals(actual, carDto);
    }

    @Test
    @DisplayName("get car by invalid id")
    public void getCarById_nonValidCar_returnException() {
        Long carId = -1L;

        Mockito.when(carRepository.getCarById(carId)).thenThrow(
                new EntityNotFoundException(("Can`t find car by id " + carId)));

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.getCarById(carId)
        );

        String expected = "Can`t find car by id " + carId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("update car by invalid id")
    public void update_invalidCar_returnException() {
        CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto(
                "Audi", "Q7",1, "HATCHBACK", BigDecimal.valueOf(200));

        Long carId = 1L;

        Mockito.when(carRepository.getCarById(carId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> carService.update(createCarRequestDto, carId));

        String expected = "Can`t find car by id " + carId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("delete car")
    public void deleteById_withValidId_deleted() {
        Long id = 1L;
        carService.deleteById(id);
        verify(carRepository, times(1)).deleteById(id);
    }
}
