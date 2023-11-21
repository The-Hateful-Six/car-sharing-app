package thehatefulsix.carsharingapp.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateDto(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName
) {
}
