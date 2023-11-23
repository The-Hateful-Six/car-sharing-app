package thehatefulsix.carsharingapp.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import thehatefulsix.carsharingapp.dto.payment.CreatePaymentRequestDto;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
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

    @Sql(scripts = "classpath:database/payments/add-entities-to-database.sql")
    @Sql(scripts = "classpath:database/payments/delete-all-from-database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "test@gmail.com", authorities = {"CLIENT"})
    @Test
    void createPaymentSession_WithValidData_ShouldReturn() throws Exception {
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(
                1L,
                "PAYMENT"
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        System.out.println(jsonRequest);

        mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Sql(scripts = "classpath:database/payments/add-entities-to-database.sql")
    @Sql(scripts = "classpath:database/payments/add-payment-to-database.sql")
    @Sql(scripts = "classpath:database/payments/delete-all-from-database.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getSuccessfulResponse_WithValidData_ShouldReturn() throws Exception {
        PaymentDto expected = new PaymentDto(
                1L,
                "PAID",
                "PAYMENT",
                1L,
                180,
                null);

        String sessionId = "random_session_id";
        MvcResult mvcResult = mockMvc.perform(get("/payments/success")
                        .param("session_id", sessionId)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn();

        PaymentDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), PaymentDto.class);
        assertEquals(expected, actual);
    }
}
