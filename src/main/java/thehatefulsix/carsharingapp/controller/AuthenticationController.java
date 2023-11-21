package thehatefulsix.carsharingapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.user.UserLoginRequestDto;
import thehatefulsix.carsharingapp.dto.user.UserLoginResponseDto;
import thehatefulsix.carsharingapp.dto.user.UserRegistrationRequestDto;
import thehatefulsix.carsharingapp.dto.user.UserResponseDto;
import thehatefulsix.carsharingapp.exception.RegistrationException;
import thehatefulsix.carsharingapp.security.AuthenticationService;
import thehatefulsix.carsharingapp.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
