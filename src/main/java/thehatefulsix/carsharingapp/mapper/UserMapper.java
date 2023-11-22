package thehatefulsix.carsharingapp.mapper;

import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import thehatefulsix.carsharingapp.dto.user.UserRegistrationRequestDto;
import thehatefulsix.carsharingapp.dto.user.UserResponseDto;
import thehatefulsix.carsharingapp.dto.user.UserRoleUpdateDto;
import thehatefulsix.carsharingapp.dto.user.UserUpdateDto;
import thehatefulsix.carsharingapp.model.user.Role;
import thehatefulsix.carsharingapp.model.user.User;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        implementationPackage = "<PACKAGE_NAME>.impl"
)
public interface UserMapper {
    User toUser(UserRegistrationRequestDto requestDto);

    UserResponseDto toUserResponseDto(User user);

    void updateUser(UserUpdateDto userUpdateDto, @MappingTarget User user);

    void updateUserRole(UserRoleUpdateDto roleUpdateDto, @MappingTarget User user);

    @AfterMapping
    default void setRoleIds(UserRoleUpdateDto updateDto, @MappingTarget User user) {
        user.setRoles(updateDto.roleId().stream()
                .map(Role::new)
                .collect(Collectors.toSet()));
    }
}
