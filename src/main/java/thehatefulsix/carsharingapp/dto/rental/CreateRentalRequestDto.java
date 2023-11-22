package thehatefulsix.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateRentalRequestDto(
        @NotNull
        LocalDate rentalDate,

        @NotNull
        LocalDate returnDate,

        @NotNull @Positive
        Long carId
) {
}
