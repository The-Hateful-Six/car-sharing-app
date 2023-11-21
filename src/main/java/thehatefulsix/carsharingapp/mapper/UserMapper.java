package thehatefulsix.carsharingapp.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import thehatefulsix.carsharingapp.dto.user.UserRegistrationRequestDto;
import thehatefulsix.carsharingapp.dto.user.UserRegistrationResponseDto;
import thehatefulsix.carsharingapp.model.User;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        implementationPackage = "<PACKAGE_NAME>.impl"
)
public interface UserMapper {
    User toUser(UserRegistrationRequestDto requestDto);

    UserRegistrationResponseDto toUserResponseDto(User user);
}
