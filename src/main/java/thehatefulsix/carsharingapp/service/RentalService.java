package thehatefulsix.carsharingapp.service;

import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import java.time.LocalDate;
import java.util.List;

@Service
public interface RentalService {

    RentalDto save(CreateRentalRequestDto requestDto);

    List<RentalDto> getAllByUserIdAndIsActive(Long userId, boolean isActive);

    RentalDto getRentalById(Long rentalId);

    RentalDto addActualReturnTime(Long rentalId, LocalDate actualReturnDate);
}
