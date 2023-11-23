package thehatefulsix.carsharingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import thehatefulsix.carsharingapp.exception.AdvertCreateException;

public class AdsCheckValidator implements ConstraintValidator<AdsCheck, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(AdsCheck constraintAnnotation) {
        firstFieldName = constraintAnnotation.text();
        secondFieldName = constraintAnnotation.photoUrl();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        String text;
        String photoUrl;
        try {
            Field textField = object.getClass().getDeclaredField(firstFieldName);
            textField.setAccessible(true);
            text = (String) textField.get(object);
            Field secondField = object.getClass().getDeclaredField(secondFieldName);
            secondField.setAccessible(true);
            photoUrl = (String) secondField.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AdvertCreateException("Something went wrong while cheating an ad");
        }
        if ((photoUrl == null && text == null)
                || (photoUrl != null && !photoUrl.endsWith(".jpg"))) {
            return false;
        }
        return true;
    }
}
