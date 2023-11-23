package thehatefulsix.carsharingapp.exception;

public class AdvertCreateException extends RuntimeException {
    public AdvertCreateException(String message) {
        super(message);
    }

    public AdvertCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
