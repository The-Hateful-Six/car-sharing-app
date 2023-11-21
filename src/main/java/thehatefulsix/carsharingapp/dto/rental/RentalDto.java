package thehatefulsix.carsharingapp.dto.rental;

import java.time.LocalDate;

public record RentalDto(
        Long id,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate,
        Long carId,
        Long userId,
        boolean isActive
) {
}
