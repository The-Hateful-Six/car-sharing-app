package thehatefulsix.carsharingapp.payment.strategy.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import thehatefulsix.carsharingapp.model.Car;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.payment.strategy.OperationHandler;

public class PaymentOperation implements OperationHandler {

    @Override
    public BigDecimal getTotalPrice(Rental rental, Car car) {
        long daysBetween = ChronoUnit.DAYS.between(
                rental.getReturnDate(), rental.getRentalDate());
        return car.getDailyFee().multiply(BigDecimal.valueOf(daysBetween));
    }
}
