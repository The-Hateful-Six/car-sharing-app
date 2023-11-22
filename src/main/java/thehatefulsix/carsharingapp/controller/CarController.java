package thehatefulsix.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.car.CarDto;
import thehatefulsix.carsharingapp.dto.car.CreateCarRequestDto;
import thehatefulsix.carsharingapp.service.CarService;

@Tag(name = "Cars management",
        description = "Endpoints for managing cars")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cars")
public class CarController {
    private final CarService carService;

    @Operation(summary = "Get all cars",
            description = "Get a list of all cars")
    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping
    public List<CarDto> getAll(@ParameterObject @PageableDefault(size = 5) Pageable pageable) {
        return carService.findAll(pageable);
    }

    @Operation(summary = "Get car by id",
            description = "Get car's detailed information by id")
    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/{id}")
    public CarDto getCarById(@PathVariable @Positive Long id) {
        return carService.getCarById(id);
    }

    @Operation(summary = "Create new car", description = "Create new car")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping
    public CarDto createCar(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @Operation(summary = "Update car", description = "Update car by id")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping("/{id}")
    public CarDto update(@RequestBody @Valid CreateCarRequestDto carRequestDto,
                         @PathVariable @Positive Long id) {
        return carService.update(carRequestDto, id);
    }

    @Operation(summary = "Delete car", description = "Delete car by id")
    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        carService.deleteById(id);
    }
}
