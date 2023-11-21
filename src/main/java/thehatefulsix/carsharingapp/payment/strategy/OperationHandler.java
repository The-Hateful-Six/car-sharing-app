package thehatefulsix.carsharingapp.payment.strategy;

import java.math.BigDecimal;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.car.Car;

public interface OperationHandler {
    BigDecimal getTotalPrice(Rental rental, Car car);
}
