package thehatefulsix.carsharingapp.controller;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.mapper.RentalMapper;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.service.CarService;
import thehatefulsix.carsharingapp.service.RentalService;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;
    private final RentalMapper rentalMapper;
    private final CarService carService;

    @PostMapping
    public RentalDto createRental(@RequestBody CreateRentalRequestDto requestDto) {
        Long carId = requestDto.carId();
        //update cars quantity;
        return rentalService.save(requestDto);
    }

    @PostMapping
    public List<RentalDto> getAllByUserIdAndIsActive(Long userId, boolean isActive) {
        return rentalService.getAllByUserIdAndIsActive(userId, isActive);
    }

    @PostMapping
    public RentalDto getRental() {
        return rentalService.getRentalById(getUserId());
    }

    @PostMapping
    public RentalDto updateReturnTime(LocalDate actualReturnDate) {
        return rentalService.addActualReturnTime(getUserId(), actualReturnDate);
    }

    Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
