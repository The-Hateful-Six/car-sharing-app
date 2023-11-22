package thehatefulsix.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.user.UserResponseDto;
import thehatefulsix.carsharingapp.dto.user.UserRoleUpdateDto;
import thehatefulsix.carsharingapp.dto.user.UserUpdateDto;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.service.UserService;

@Tag(name = "Users management",
        description = "Endpoints for managing users")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update user role",
            description = "Update user role by users id")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping("/{id}/role")
    public void updateRole(@PathVariable @Positive Long id,
                           @RequestBody @Valid UserRoleUpdateDto roleUpdateDto) {
        userService.updateRole(id, roleUpdateDto);
    }

    @Operation(summary = "Get profile info",
            description = "Get user profile info")
    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/me")
    public UserResponseDto getProfileInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfileInfo(user.getId());
    }

    @Operation(summary = "Update profile info",
            description = "Update user profile info")
    @PreAuthorize("hasAuthority('CLIENT')")
    @PutMapping("/me")
    public UserResponseDto updateProfile(Authentication authentication,
                                         @RequestBody @Valid UserUpdateDto updateDto) {
        User user = (User) authentication.getPrincipal();
        return userService.update(user.getId(), updateDto);
    }
}
