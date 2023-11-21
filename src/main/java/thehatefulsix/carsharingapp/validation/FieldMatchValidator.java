package thehatefulsix.carsharingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String fieldToMatch;

    @Override
    public void initialize(FieldMatch fieldMatch) {
        this.field = fieldMatch.field();
        this.fieldToMatch = fieldMatch.fieldToMatch();
    }

    @Override
    public boolean isValid(Object object,
                           ConstraintValidatorContext constraintValidatorContext) {
        Object password = new BeanWrapperImpl(object).getPropertyValue(field);
        Object repeatPassword = new BeanWrapperImpl(object).getPropertyValue(fieldToMatch);
        return password != null && password.equals(repeatPassword);
    }
}
