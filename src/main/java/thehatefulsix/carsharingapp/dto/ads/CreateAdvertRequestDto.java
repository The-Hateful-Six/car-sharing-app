package thehatefulsix.carsharingapp.dto.ads;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import thehatefulsix.carsharingapp.validation.AdsCheck;

@AdsCheck(text = "text", photoUrl = "photoUrl")
public record CreateAdvertRequestDto(String text,
                                     String photoUrl,
                                     @NotNull
                                     LocalDateTime sendTime) {
}
