package thehatefulsix.carsharingapp.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import thehatefulsix.carsharingapp.dto.rental.CreateRentalRequestDto;
import thehatefulsix.carsharingapp.dto.rental.RentalDto;
import thehatefulsix.carsharingapp.model.Rental;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        implementationPackage = "<PACKAGE_NAME>.impl")
public interface RentalMapper {

    RentalDto toDto(Rental rental);

    Rental toRental(CreateRentalRequestDto requestDto);
}
