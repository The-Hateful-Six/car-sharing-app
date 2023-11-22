package thehatefulsix.carsharingapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import thehatefulsix.carsharingapp.dto.user.UserRegistrationRequestDto;
import thehatefulsix.carsharingapp.dto.user.UserResponseDto;
import thehatefulsix.carsharingapp.dto.user.UserRoleUpdateDto;
import thehatefulsix.carsharingapp.dto.user.UserUpdateDto;
import thehatefulsix.carsharingapp.exception.EntityNotFoundException;
import thehatefulsix.carsharingapp.exception.RegistrationException;
import thehatefulsix.carsharingapp.mapper.UserMapper;
import thehatefulsix.carsharingapp.mapper.impl.UserMapperImpl;
import thehatefulsix.carsharingapp.model.user.Role;
import thehatefulsix.carsharingapp.model.user.RoleName;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.repository.RoleRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;
import thehatefulsix.carsharingapp.service.impl.UserServiceImpl;
import thehatefulsix.carsharingapp.util.EmailNotificationSender;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmailNotificationSender emailNotificationSender;
    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void register_WithValidData_ShouldReturnUserResponseDto() {
        Long id = 1L;

        Role role = new Role();
        role.setId(id);
        role.setName(RoleName.CLIENT);

        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("12345678");

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "test@example.com",
                "12345678",
                "12345678",
                "John",
                "Doe"
        );

        UserResponseDto expected = new UserResponseDto(
                id,
                "test@example.com",
                "John",
                "Doe"
        );

        when(userMapper.toUser(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(expected);
        doNothing().when(emailNotificationSender).sendRegistrationNotification(user);
        when(roleRepository.findByName(RoleName.CLIENT)).thenReturn(role);

        UserResponseDto actual = userService.register(requestDto);

        assertEquals(actual, expected);
    }

    @Test
    public void register_WithInvalidData_ShouldThrowException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("12345678");

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "test@example.com",
                "12345678",
                "12345678",
                "John",
                "Doe"
        );

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        Exception exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(requestDto)
        );
        String expected = "Unable to complete registration";
        String actual = exception.getMessage();
        assertEquals(actual, expected);

    }

    @Test
    public void update_WithValidData_ShouldReturnUserResponseDto() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("12345678");

        UserUpdateDto updateDto = new UserUpdateDto(
                "Test first name",
                "Test last name"
        );

        String email = "test@example.com";

        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDto updated = userService.update(updateDto);

        assertThat(updated).hasFieldOrPropertyWithValue("firstName", updateDto.firstName())
                .hasFieldOrPropertyWithValue("lastName", updateDto.lastName());
    }

    @Test
    public void update_WithInvalidData_ShouldThrowException() {
        UserUpdateDto updateDto = new UserUpdateDto(
                "Test first name",
                "Test last name"
        );

        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.update(updateDto)
        );
        String expected = "User not found";
        String actual = exception.getMessage();
        assertEquals(actual, expected);
    }

    @Test
    public void getProfileInfo_WithValidData_ShouldReturnUserResponseDto() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("12345678");

        Long id = 1L;

        UserResponseDto expected = new UserResponseDto(
                id,
                "test@example.com",
                "John",
                "Doe"
        );

        String email = "test@example.com";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn(email);
        when(userMapper.toUserResponseDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.getProfileInfo();

        assertEquals(actual, expected);
    }

    @Test
    public void getProfileInfo_WithInvalidData_ShouldThrowException() {
        String email = "test@example.com";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn(email);

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getProfileInfo()
        );

        String expected = "User not found";
        String actual = exception.getMessage();

        assertEquals(actual, expected);
    }

    @Test
    public void updateRole_WithValidData_ShouldDoNothing() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("12345678");

        UserRoleUpdateDto roleUpdateDto = new UserRoleUpdateDto(
                List.of(2L)
        );

        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.updateRole(id, roleUpdateDto);

        verify(userMapper, times(1)).updateUserRole(roleUpdateDto, user);
    }

    @Test
    public void updateRole_WithInvalidData_ShouldThrowException() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserRoleUpdateDto roleUpdateDto = new UserRoleUpdateDto(
                List.of(2L)
        );

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateRole(id, roleUpdateDto)
        );

        String expected = "User not found";
        String actual = exception.getMessage();

        assertEquals(actual, expected);
    }
}
