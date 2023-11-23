package thehatefulsix.carsharingapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.model.Rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/rentals/add-default-car.sql")
@Sql(scripts = "classpath:database/users/add-default-user.sql")
@Sql(scripts = "classpath:database/rentals/add-default-rental.sql")
@Sql(scripts = "classpath:database/rentals/delete-all-from-rentals-and-cars.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "classpath:database/users/delete-all-from-users-table.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RentalControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(scripts = "classpath:database/rentals/delete-all-from-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "first@example.com", authorities = {"CLIENT"})
    @Test
    void createRental_WithValidRequestDto_Success() throws Exception {
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto(LocalDate.of(2023,
                11, 19), LocalDate.of(2023, 11, 20), 1L);
        RentalDto expected = new RentalDto(1L, LocalDate.of(2023,
                11, 19), LocalDate.of(2023, 11, 20), null, 1L, 1L, true);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn();

        RentalDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RentalDto.class);
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.carId(), actual.carId());
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.rentalDate(), actual.rentalDate());
        assertEquals(expected.returnDate(), actual.returnDate());
        assertEquals(expected.isActive(), actual.isActive());
        assertEquals(expected.actualReturnDate(), actual.actualReturnDate());
    }

    @WithMockUser(username = "manager", authorities = {"MANAGER"})
    @Test
    void getAllByUserIdAndIsActive_ValidUserIdAndIsActive_Success() throws Exception {
        Rental rental = new Rental();
        Long rentalId = 1L;
        rental.setId(rentalId);
        rental.setRentalDate(LocalDate.of(2023, 11, 20));
        rental.setReturnDate(LocalDate.of(2023, 11, 21));
        rental.setCarId(1L);
        rental.setUserId(1L);
        rental.setIsActive(true);
        rental.setIsDeleted(false);

        MvcResult mvcResult = mockMvc.perform(get("/rentals")
                        .param("userId", "1")
                        .param("isActive", "true"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @WithMockUser(username = "manager", authorities = {"MANAGER"})
    @Test
    void getRental_ValidRentalId_Success() throws Exception {
        Long id = 1L;
        RentalDto expected = new RentalDto(id, LocalDate.of(2023, 11, 19),
                LocalDate.of(2023, 11, 20),
                null, 1L, 1L, true);

        MvcResult mvcResult = mockMvc.perform(get("/rentals/{id}", id))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        RentalDto actual = objectMapper.readValue(jsonResponse, RentalDto.class);
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.carId(), actual.carId());
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.rentalDate(), actual.rentalDate());
        assertEquals(expected.returnDate(), actual.returnDate());
        assertEquals(expected.isActive(), actual.isActive());
        assertEquals(expected.actualReturnDate(), actual.actualReturnDate());
    }

    @WithMockUser(username = "manager", authorities = {"MANAGER"})
    @Test
    void updateReturnTime_ValidRequest_Success() throws Exception {
        Long rentalId = 1L;
        RentalDto expected = new RentalDto(1L, LocalDate.of(2023, 11, 19),
                LocalDate.of(2023, 11, 20),
                LocalDate.now(), 1L, 1L, false);

        MvcResult mvcResult = mockMvc.perform(post("/rentals/{id}/return", rentalId))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        RentalDto actual = objectMapper.readValue(jsonResponse, RentalDto.class);
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.carId(), actual.carId());
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.rentalDate(), actual.rentalDate());
        assertEquals(expected.returnDate(), actual.returnDate());
        assertEquals(expected.isActive(), actual.isActive());
        assertEquals(expected.actualReturnDate(), actual.actualReturnDate());
    }
}
