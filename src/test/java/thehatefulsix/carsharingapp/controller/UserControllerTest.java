package thehatefulsix.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import thehatefulsix.carsharingapp.dto.user.UserResponseDto;
import thehatefulsix.carsharingapp.dto.user.UserRoleUpdateDto;
import thehatefulsix.carsharingapp.dto.user.UserUpdateDto;

@Sql(scripts = "classpath:database/users/add-default-user.sql")
@Sql(scripts = "classpath:database/users/delete-all-from-users-table.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
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

    @AfterEach
    void afterEach(
            @Autowired DataSource dataSource
    ) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-default-user.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/users/delete-all-from-users-table.sql"
                    )
            );
        }
    }

    @WithMockUser(username = "manager", authorities = {"MANAGER"})
    @Test
    void updateRole_WithValidData_ShouldDoNothing() throws Exception {
        UserRoleUpdateDto roleUpdateDto = new UserRoleUpdateDto(
                List.of(2L)
        );

        String jsonRequest = objectMapper.writeValueAsString(roleUpdateDto);

        MvcResult result = mockMvc.perform(
                        put("/users/1/role")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @WithMockUser(username = "first@example.com", authorities = {"CLIENT"})
    @Test
    void getProfileInfo_WithValidData_ShouldReturnUserResponseDto() throws Exception {
        Long id = 1L;

        UserResponseDto expected = new UserResponseDto(
                id,
                "first@example.com",
                "First",
                "User"
        );

        MvcResult result = mockMvc.perform(
                        get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "first@example.com", authorities = {"CLIENT"})
    @Test
    void updateProfile_WithValidData_ShouldReturnUserResponseDto() throws Exception {
        Long id = 1L;

        UserResponseDto expected = new UserResponseDto(
                id,
                "first@example.com",
                "Test",
                "Test"
        );

        UserUpdateDto updateDto = new UserUpdateDto(
                "Test",
                "Test"
        );

        String jsonRequest = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(
                        put("/users/me")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertEquals(expected, actual);
    }
}
