package thehatefulsix.carsharingapp.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import thehatefulsix.carsharingapp.model.payment.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.rentalId IN "
            + "(SELECT r.id FROM Rental r WHERE r.userId = :userId)")
    List<Payment> findPaymentsByUserId(@Param("userId") Long userId, Pageable pageable);
}
