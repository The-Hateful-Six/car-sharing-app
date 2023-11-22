package thehatefulsix.carsharingapp.dto.user;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UserRoleUpdateDto(
        @NotNull List<Long> roleId
) {
}
