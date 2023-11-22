package thehatefulsix.carsharingapp.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;

public interface RentalService {

    RentalDto save(CreateRentalRequestDto requestDto);

    List<RentalDto> getAllByUserIdAndIsActive(Long userId, boolean isActive, Pageable pageable);

    RentalDto getRentalById(Long rentalId);

    void sendNotificationAboutRentDelay();

    RentalDto addActualReturnTime(Long rentalId);
}
