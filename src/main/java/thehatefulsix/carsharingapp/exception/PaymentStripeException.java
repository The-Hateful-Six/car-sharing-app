package thehatefulsix.carsharingapp.exception;

public class PaymentStripeException extends RuntimeException {
    public PaymentStripeException(String message, Throwable cause) {
        super(message, cause);
    }
}
