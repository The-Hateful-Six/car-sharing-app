package thehatefulsix.carsharingapp.model.advert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Table(name = "adverts")
@Entity
@Data
public class Advert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String photoUrl;
    @Column(nullable = false)
    private LocalDateTime sendTime;
    @Column(nullable = false)
    private Boolean isActive = true;
}
