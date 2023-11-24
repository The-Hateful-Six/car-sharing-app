package thehatefulsix.carsharingapp.repository;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import thehatefulsix.carsharingapp.model.rental.Rental;

@DataJpaTest
@Sql(scripts = "classpath:database/rentals/add-default-car.sql")
@Sql(scripts = "classpath:database/users/add-default-user.sql")
@Sql(scripts = "classpath:database/rentals/add-default-rental.sql")
@Sql(scripts = "classpath:database/rentals/delete-all-from-rentals-and-cars.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "classpath:database/users/delete-all-from-users-table.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    public void getAllByUserIdAndIsActive_UserExistsAndRentalIsActive_ReturnsRentalList() {
        Rental rental = new Rental();
        rental.setUserId(1L);
        rental.setRentalDate(LocalDate.of(2023, 11, 19));
        rental.setReturnDate(LocalDate.of(2023, 11, 20));
        rental.setCarId(1L);
        rental.setUserId(1L);
        rental.setIsActive(true);
        rental.setIsDeleted(false);
        List<Rental> expected = List.of(rental);
        List<Rental> actual = rentalRepository.getAllByUserIdAndIsActive(1L,
                true, PageRequest.of(0,10));
        Assertions.assertEquals(expected.get(0).getUserId(), actual.get(0).getUserId());
        Assertions.assertEquals(expected.get(0).getRentalDate(), actual.get(0).getRentalDate());
        Assertions.assertEquals(expected.get(0).getReturnDate(), actual.get(0).getReturnDate());
        Assertions.assertEquals(expected.get(0).getActualReturnDate(),
                actual.get(0).getActualReturnDate());
        Assertions.assertEquals(expected.get(0).getCarId(), actual.get(0).getCarId());
        Assertions.assertEquals(expected.get(0).getUserId(), actual.get(0).getUserId());
        Assertions.assertEquals(expected.get(0).getIsActive(), actual.get(0).getIsActive());
        Assertions.assertEquals(expected.get(0).getIsDeleted(), actual.get(0).getIsDeleted());
    }

    @Test
    public void getAllByUserIdAndIsActive_WithNotExistingId_ReturnsRentalList() {
        List<Rental> actual = rentalRepository.getAllByUserIdAndIsActive(100L,
                true, PageRequest.of(0,10));
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    public void getAllByUserIdAndIsActive_WithNotValidIsActive_ReturnsRentalList() {
        List<Rental> actual = rentalRepository.getAllByUserIdAndIsActive(1L,
                false, PageRequest.of(0, 10));
        Assertions.assertEquals(0, actual.size());
    }
}
