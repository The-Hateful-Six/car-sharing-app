package thehatefulsix.carsharingapp.service;

import thehatefulsix.carsharingapp.dto.user.UserRegistrationRequestDto;
import thehatefulsix.carsharingapp.dto.user.UserResponseDto;
import thehatefulsix.carsharingapp.dto.user.UserRoleUpdateDto;
import thehatefulsix.carsharingapp.dto.user.UserUpdateDto;
import thehatefulsix.carsharingapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto registrationRequest)
            throws RegistrationException;

    UserResponseDto update(Long id, UserUpdateDto updateRequest);

    UserResponseDto getProfileInfo(Long id);

    void updateRole(Long id, UserRoleUpdateDto roleUpdateDto);
}
