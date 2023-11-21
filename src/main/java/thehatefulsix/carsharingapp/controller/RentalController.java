package thehatefulsix.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.car.CarDto;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.service.CarService;
import thehatefulsix.carsharingapp.service.RentalService;

@Tag(name = "Rentals management",
        description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
@Validated
public class RentalController {
    private final RentalService rentalService;
    private final CarService carService;

    @Operation(summary = "Create new rental",
            description = "Add a new rental (decrease car inventory by 1)")
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalDto createRental(@RequestBody @Valid CreateRentalRequestDto requestDto) {
        Long carId = requestDto.carId();
        CarDto car = carService.getCarById(carId);
        car.setInventory(car.getInventory() - 1);
        return rentalService.save(requestDto);
    }

    @Operation(summary = "Get rentals by user ID",
            description = "Get rentals by user ID and whether the rental is still active or not")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping()
    public List<RentalDto> getAllByUserIdAndIsActive(
            @RequestParam @Positive Long userId,
            @RequestParam boolean isActive,
            @ParameterObject @PageableDefault(size = 5) Pageable pageable) {
        return rentalService.getAllByUserIdAndIsActive(userId, isActive, pageable);
    }

    @Operation(summary = "Get rental by id",
            description = "Get specific rental by rental's id")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public RentalDto getRental(@PathVariable @Positive Long userId) {
        return rentalService.getRentalById(userId);
    }

    @Operation(summary = "Set return date",
            description = "Set actual return date (increase car inventory by 1)")
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/return")
    public RentalDto updateReturnTime() {
        Long carId = getRental(getUserId()).carId();
        CarDto car = carService.getCarById(carId);
        car.setInventory(car.getInventory() + 1);
        return rentalService.addActualReturnTime(getUserId(), LocalDate.now());
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
