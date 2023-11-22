package thehatefulsix.carsharingapp.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import thehatefulsix.carsharingapp.model.Rental;
import thehatefulsix.carsharingapp.model.user.User;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r WHERE r.userId = :userId AND r.isActive = :isActive")
    List<Rental> getAllByUserIdAndIsActive(Long userId, boolean isActive, Pageable pageable);

    @Query("SELECT DISTINCT r.userId FROM Rental r WHERE r.returnDate = :yesterday AND r.actualReturnDate IS NULL AND r.isActive = true")
    List<User> findUsersWithUnreturnedCars(@Param("yesterday") LocalDate yesterday);
}
