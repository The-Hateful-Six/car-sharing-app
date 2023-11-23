package thehatefulsix.carsharingapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import thehatefulsix.carsharingapp.dto.car.CarDto;
import thehatefulsix.carsharingapp.model.car.CarType;

@Sql(scripts = {"classpath:database/rentals/add-default-car.sql"})
@Sql(scripts = {"classpath:database/rentals/delete-all-from-rentals-and-cars.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTests {
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

    @Test
    void getAllCars_WithValidData_ShouldReturnCarDto() throws Exception {
        CarDto expected = new CarDto();
        expected.setId(1L);
        expected.setModel("A 3");
        expected.setBrand("Audi");
        expected.setCarType(CarType.SEDAN);
        expected.setInventory(2);
        expected.setDailyFee(BigDecimal.valueOf(300));
        MvcResult result = mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = Arrays.stream(objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto[].class)).toList().get(0);
        assertThat(actual).isEqualTo(expected);
    }

    @WithMockUser(username = "test@gmail.com", authorities = {"CLIENT"})
    @Test
    void getCarById_WithValidData_ShouldReturnCarDto() throws Exception {
        CarDto expected = new CarDto();
        expected.setId(1L);
        expected.setModel("A 3");
        expected.setBrand("Audi");
        expected.setCarType(CarType.SEDAN);
        expected.setInventory(2);
        expected.setDailyFee(BigDecimal.valueOf(300));
        MvcResult result = mockMvc.perform(get("/cars/1"))
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @WithMockUser(username = "test@gmail.com", authorities = {"MANAGER"})
    void deleteCategoryById_validId() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/cars/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
