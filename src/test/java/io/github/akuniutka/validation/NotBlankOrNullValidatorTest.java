package io.github.akuniutka.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("NotBlankOrNullValidator Unit Tests")
class NotBlankOrNullValidatorTest {

    private static final ConstraintValidatorContext CONTEXT = null;

    private final NotBlankOrNullValidator validator = new NotBlankOrNullValidator();

    @DisplayName("""
            Given a sequence is null,
            when check if the sequence is valid,
            then return true
            """)
    @Test
    void whenIsValidAndCharSequenceIsNull_ThenReturnTrue() {

        final boolean isValid = validator.isValid(null, CONTEXT);

        then(isValid).isTrue();
    }

    @DisplayName("""
            Given a sequence is empty,
            when check if the sequence is valid,
            then return false
            """)
    @Test
    void whenIsValidAndCharSequenceIsEmpty_ThenReturnFalse() {

        final boolean isValid = validator.isValid("", CONTEXT);

        then(isValid).isFalse();
    }

    @DisplayName("""
            Given a sequence is blank,
            when check if the sequence is valid,
            then return false
            """)
    @Test
    void whenIsValidAndCharSequenceIsBlank_ThenReturnFalse() {

        final boolean isValid = validator.isValid(" ", CONTEXT);

        then(isValid).isFalse();
    }

    @DisplayName("""
            Given a sequence is not blank,
            when check if the sequence is valid,
            then return true
            """)
    @Test
    void whenIsValidAndCharSequenceNotBlank_ThenReturnTrue() {

        final boolean isValid = validator.isValid("test", CONTEXT);

        then(isValid).isTrue();
    }
}
