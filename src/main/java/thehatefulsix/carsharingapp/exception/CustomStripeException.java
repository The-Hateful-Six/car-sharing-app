package thehatefulsix.carsharingapp.exception;

public class CustomStripeException extends RuntimeException {

    public CustomStripeException(String message) {
        super(message);
    }

    public CustomStripeException(String message, Throwable cause) {
        super(message, cause);
    }
}
