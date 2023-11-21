package thehatefulsix.carsharingapp.payment.strategy;

import java.math.BigDecimal;
import thehatefulsix.carsharingapp.model.Car;
import thehatefulsix.carsharingapp.model.Rental;

public interface OperationHandler {
    BigDecimal getTotalPrice(Rental rental, Car car);
}
