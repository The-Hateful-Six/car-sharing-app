package thehatefulsix.carsharingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thehatefulsix.carsharingapp.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
}
