package thehatefulsix.carsharingapp.payment.strategy.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import thehatefulsix.carsharingapp.model.car.Car;
import thehatefulsix.carsharingapp.model.rental.Rental;
import thehatefulsix.carsharingapp.payment.strategy.OperationHandler;

public class FineOperation implements OperationHandler {
    private static final Long CENT_MULTIPLIER = 100L;

    @Override
    public BigDecimal getTotalPrice(Rental rental, Car car) {
        long daysBetween = ChronoUnit.DAYS.between(
                rental.getReturnDate(), rental.getActualReturnDate());
        return car.getDailyFee().multiply(BigDecimal.valueOf(daysBetween))
                .multiply(BigDecimal.valueOf(CENT_MULTIPLIER));
    }
}
