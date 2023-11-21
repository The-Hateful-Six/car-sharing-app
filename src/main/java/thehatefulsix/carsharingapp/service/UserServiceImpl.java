package thehatefulsix.carsharingapp.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import thehatefulsix.carsharingapp.dto.user.UserRegistrationRequestDto;
import thehatefulsix.carsharingapp.dto.user.UserRegistrationResponseDto;
import thehatefulsix.carsharingapp.exception.RegistrationException;
import thehatefulsix.carsharingapp.mapper.UserMapper;
import thehatefulsix.carsharingapp.model.RoleName;
import thehatefulsix.carsharingapp.model.User;
import thehatefulsix.carsharingapp.repository.RoleRepository;
import thehatefulsix.carsharingapp.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserRegistrationResponseDto register(
            UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.existsByEmail(request.email())) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName(RoleName.CLIENT)));
        return userMapper.toUserResponseDto(userRepository.save(user));
    }
}
