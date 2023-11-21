package thehatefulsix.carsharingapp.dto.payment;

public record PaymentDto(
        Long id,
        String status,
        String type,
        Long rentalId,
        Integer amountToPay,
        String sessionUrl
) {
}
