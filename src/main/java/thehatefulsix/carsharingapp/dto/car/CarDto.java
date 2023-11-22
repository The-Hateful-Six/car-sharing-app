package thehatefulsix.carsharingapp.dto.car;

import java.math.BigDecimal;
import lombok.Data;
import thehatefulsix.carsharingapp.model.car.CarType;

@Data
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private CarType carType;
    private Integer inventory;
    private BigDecimal dailyFee;
}
