package io.github.akuniutka.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class NotBlankOrNullValidatorTest {

    private static final ConstraintValidatorContext CONTEXT = null;

    private final NotBlankOrNullValidator validator = new NotBlankOrNullValidator();

    @Test
    void whenIsValidAndCharSequenceIsNull_ThenReturnTrue() {

        final boolean isValid = validator.isValid(null, CONTEXT);

        then(isValid).isTrue();
    }

    @Test
    void whenIsValidAndCharSequenceIsEmpty_ThenReturnFalse() {

        final boolean isValid = validator.isValid("", CONTEXT);

        then(isValid).isFalse();
    }

    @Test
    void whenIsValidAndCharSequenceIsBlank_ThenReturnFalse() {

        final boolean isValid = validator.isValid(" ", CONTEXT);

        then(isValid).isFalse();
    }

    @Test
    void whenIsValidAndCharSequenceNotBlank_ThenReturnTrue() {

        final boolean isValid = validator.isValid("test", CONTEXT);

        then(isValid).isTrue();
    }
}
