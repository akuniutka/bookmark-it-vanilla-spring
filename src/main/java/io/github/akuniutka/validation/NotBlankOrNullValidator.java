package io.github.akuniutka.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankOrNullValidator implements ConstraintValidator<NotBlankOrNull, CharSequence> {

    @Override
    public boolean isValid(final CharSequence charSequence, final ConstraintValidatorContext context) {
        return charSequence == null || !charSequence.toString().isBlank();
    }
}
