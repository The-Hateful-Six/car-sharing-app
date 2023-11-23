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
import thehatefulsix.carsharingapp.dto.ads.AdvertDto;
import thehatefulsix.carsharingapp.dto.ads.CreateAdvertRequestDto;
import thehatefulsix.carsharingapp.service.AdvertService;

@Tag(name = "Adverts",
        description = "Endpoints for advert's managing")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/adverts")
public class AdvertController {
    private final AdvertService advertService;

    @Operation(summary = "Get all ads",
            description = "Get a list of all ads")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping
    public List<AdvertDto> getAll(@ParameterObject @PageableDefault(size = 5) Pageable pageable) {
        return advertService.findAll(pageable);
    }

    @Operation(summary = "Get an ad by id",
            description = "Get ad's detailed information by id")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/{id}")
    public AdvertDto getAddById(@PathVariable @Positive Long id) {
        return advertService.getAddById(id);
    }

    @Operation(summary = "Create a new add", description = "Create new add")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping
    public AdvertDto createAdd(@RequestBody @Valid CreateAdvertRequestDto requestDto) {
        return advertService.save(requestDto);
    }

    @Operation(summary = "Update add", description = "Update add by id")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping("/{id}")
    public AdvertDto update(@RequestBody @Valid CreateAdvertRequestDto requestDto,
                            @PathVariable @Positive Long id) {
        return advertService.update(requestDto, id);
    }

    @Operation(summary = "Delete add", description = "Delete add by id")
    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        advertService.deleteById(id);
    }
}
