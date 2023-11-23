package thehatefulsix.carsharingapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AdsCheckValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdsCheck {
    String message() default "Only one of fields can be null";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String text();

    String photoUrl();
}
