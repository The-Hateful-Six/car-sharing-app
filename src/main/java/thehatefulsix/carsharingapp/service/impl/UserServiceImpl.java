package thehatefulsix.carsharingapp.service.impl;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.user.UserRegistrationRequestDto;
import thehatefulsix.carsharingapp.dto.user.UserResponseDto;
import thehatefulsix.carsharingapp.dto.user.UserRoleUpdateDto;
import thehatefulsix.carsharingapp.dto.user.UserUpdateDto;
import thehatefulsix.carsharingapp.exception.EntityNotFoundException;
import thehatefulsix.carsharingapp.exception.RegistrationException;
import thehatefulsix.carsharingapp.mapper.UserMapper;
import thehatefulsix.carsharingapp.model.user.RoleName;
import thehatefulsix.carsharingapp.model.user.User;
import thehatefulsix.carsharingapp.repository.RoleRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;
import thehatefulsix.carsharingapp.service.UserService;
import thehatefulsix.carsharingapp.util.EmailNotificationSender;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final EmailNotificationSender emailNotificationSender;

    @Override
    public UserResponseDto register(
            UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.existsByEmail(request.email())) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = userMapper.toUser(request);
        emailNotificationSender.sendRegistrationNotification(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName(RoleName.CLIENT)));
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto update(UserUpdateDto updateRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        userMapper.updateUser(updateRequest, user);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getProfileInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.toUserResponseDto(userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        ));
    }

    @Transactional
    @Override
    public void updateRole(Long id, UserRoleUpdateDto roleUpdateDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        userMapper.updateUserRole(roleUpdateDto, user);
        userRepository.save(user);
    }
}
