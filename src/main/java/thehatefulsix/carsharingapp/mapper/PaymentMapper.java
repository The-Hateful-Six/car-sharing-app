package thehatefulsix.carsharingapp.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import thehatefulsix.carsharingapp.dto.payment.PaymentDto;
import thehatefulsix.carsharingapp.model.payment.Payment;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        implementationPackage = "<PACKAGE_NAME>.impl"
)
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);
}
