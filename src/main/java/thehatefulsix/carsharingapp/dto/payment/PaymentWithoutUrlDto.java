package thehatefulsix.carsharingapp.dto.payment;

public record PaymentWithoutUrlDto(
        Long id,
        String status,
        String type,
        Long rentalId,
        Integer amountToPay
) {
}
