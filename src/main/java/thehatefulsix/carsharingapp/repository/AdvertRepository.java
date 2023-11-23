package thehatefulsix.carsharingapp.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import thehatefulsix.carsharingapp.model.Advert;

public interface AdvertRepository extends JpaRepository<Advert, Long> {
    Optional<Advert> getAdsById(Long id);
}
