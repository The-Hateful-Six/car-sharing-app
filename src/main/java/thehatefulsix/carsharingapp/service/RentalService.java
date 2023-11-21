package thehatefulsix.carsharingapp.service;

import java.time.LocalDate;
import java.util.List;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;

public interface RentalService {

    RentalDto save(CreateRentalRequestDto requestDto);

    List<RentalDto> getAllByUserIdAndIsActive(Long userId, boolean isActive);

    RentalDto getRentalById(Long rentalId);

    RentalDto addActualReturnTime(Long rentalId, LocalDate actualReturnDate);
}
