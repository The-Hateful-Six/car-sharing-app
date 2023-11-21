package thehatefulsix.carsharingapp.dto.car;

import java.math.BigDecimal;
import lombok.Data;
import thehatefulsix.carsharingapp.model.car.CarType;

@Data
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private CarType typeCar = CarType.SEDAN;
    private int inventory;
    private BigDecimal dailyFee;
    private boolean isDeleted = false;
}
