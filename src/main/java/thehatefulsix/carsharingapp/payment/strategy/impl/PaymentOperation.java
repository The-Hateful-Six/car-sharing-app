package thehatefulsix.carsharingapp.payment.strategy.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.payment.strategy.OperationHandler;

public class PaymentOperation implements OperationHandler {
    private static final Long CENT_MULTIPLIER = 100L;

    @Override
    public BigDecimal getTotalPrice(Rental rental, Car car) {
        long daysBetween = ChronoUnit.DAYS.between(
                rental.getRentalDate(), rental.getReturnDate());
        return car.getDailyFee().multiply(BigDecimal.valueOf(daysBetween))
                .multiply(BigDecimal.valueOf(CENT_MULTIPLIER));
    }
}
