package thehatefulsix.carsharingapp.exception;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<String> handleEntityNotFoundExceptions(EntityNotFoundException ex) {
        return new ResponseEntity<>("entity-not-found: "
                                    + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentStripeException.class)
    protected ResponseEntity<String> handlePaymentStripeException(PaymentStripeException ex) {
        return new ResponseEntity<>("payment exception: "
                                    + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<String> handleRegistrationExceptions(RegistrationException ex) {
        return new ResponseEntity<>("registration-exception: "
                                    + ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TelegramBotException.class)
    protected ResponseEntity<String> handleTelegramBotException(TelegramBotException ex) {
        return new ResponseEntity<>("telegram-bot-exception: "
                                    + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return field + " " + message;
        }
        return e.getDefaultMessage();
    }
}
