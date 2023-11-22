package thehatefulsix.carsharingapp.dto.car;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateCarRequestDto(
        @NotNull String model,
        @NotNull String brand,
        @NotNull Integer inventory,
        @NotNull String carType,
        @NotNull BigDecimal dailyFee
) {
}
