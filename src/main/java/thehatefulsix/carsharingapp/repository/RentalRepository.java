package thehatefulsix.carsharingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thehatefulsix.carsharingapp.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
