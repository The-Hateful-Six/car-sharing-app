package thehatefulsix.carsharingapp.dto.ads;

import java.time.LocalDateTime;

public record AdvertDto(Long id,
                        String text,
                        String photoUrl,
                        LocalDateTime sendTime){

}
