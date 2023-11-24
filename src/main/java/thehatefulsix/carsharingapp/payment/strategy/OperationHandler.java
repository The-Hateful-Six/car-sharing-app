package thehatefulsix.carsharingapp.payment.strategy;

import java.math.BigDecimal;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.rental.Rental;

public interface OperationHandler {
    BigDecimal getTotalPrice(Rental rental, Car car);
}
