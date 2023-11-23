package thehatefulsix.carsharingapp.repository;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import thehatefulsix.carsharingapp.model.payment.Payment;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Sql(scripts = {"classpath:database/payments/add-entities-to-database.sql",
                    "classpath:database/payments/add-payment-to-database.sql"})
    @Sql(scripts = "classpath:database/payments/delete-all-from-database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findPaymentsByUserId_ValidData_ShouldReturnPaymentList() {
        Long id = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Payment> actual = paymentRepository.findPaymentsByUserId(id, pageRequest);
        Assertions.assertEquals(1, actual.size());
    }
}
