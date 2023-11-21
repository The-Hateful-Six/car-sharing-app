package thehatefulsix.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import thehatefulsix.carsharingapp.validation.FieldMatch;

@FieldMatch(message = "Password should match",
        field = "password",
        fieldToMatch = "repeatPassword")
public record UserRegistrationRequestDto(
        @Email
        @NotBlank
        @Size(min = 4, max = 50)
        String email,
        @NotBlank
        @Size(min = 6, max = 100)
        String password,
        @NotBlank
        @Size(min = 6, max = 100)
        String repeatPassword,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName
) {
}
