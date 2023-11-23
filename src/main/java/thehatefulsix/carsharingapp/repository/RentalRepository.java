package thehatefulsix.carsharingapp.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import thehatefulsix.carsharingapp.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r WHERE r.userId = :userId AND r.isActive = :isActive")
    List<Rental> getAllByUserIdAndIsActive(Long userId,
                                           boolean isActive, Pageable pageable);
}
