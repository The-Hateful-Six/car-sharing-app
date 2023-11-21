package thehatefulsix.carsharingapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thehatefulsix.carsharingapp.dto.user.UserResponseDto;
import thehatefulsix.carsharingapp.dto.user.UserRoleUpdateDto;
import thehatefulsix.carsharingapp.dto.user.UserUpdateDto;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    public void updateRole(Authentication authentication, UserRoleUpdateDto roleUpdateDto) {
        User user = (User) authentication.getPrincipal();
        userService.updateRole(user.getId(), roleUpdateDto);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me")
    public UserResponseDto getProfileInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfileInfo(user.getId());
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/me")
    public UserResponseDto updateProfile(Authentication authentication, UserUpdateDto updateDto) {
        User user = (User) authentication.getPrincipal();
        return userService.update(user.getId(), updateDto);
    }
}
