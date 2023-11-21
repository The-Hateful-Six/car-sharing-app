package thehatefulsix.carsharingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import thehatefulsix.carsharingapp.model.Rental;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r WHERE r.userId = :userId AND r.isActive = :isActive")
    List<Rental> getAllByUserIdAndIsActive(Long userId, boolean isActive);
}
