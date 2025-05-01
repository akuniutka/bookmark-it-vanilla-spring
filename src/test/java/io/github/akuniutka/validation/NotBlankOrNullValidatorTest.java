package io.github.akuniutka.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotBlankOrNullValidatorTest {

    private static final ConstraintValidatorContext CONTEXT = null;
    private NotBlankOrNullValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NotBlankOrNullValidator();
    }

    @Test
    void whenIsValidAndCharSequenceIsNull_ThenReturnTrue() {

        final boolean isValid = validator.isValid(null, CONTEXT);

        assertThat(isValid).isTrue();
    }

    @Test
    void whenIsValidAndCharSequenceIsEmpty_ThenReturnFalse() {

        final boolean isValid = validator.isValid("", CONTEXT);

        assertThat(isValid).isFalse();
    }

    @Test
    void whenIsValidAndCharSequenceIsBlank_ThenReturnFalse() {

        final boolean isValid = validator.isValid(" ", CONTEXT);

        assertThat(isValid).isFalse();
    }

    @Test
    void whenIsValidAndCharSequenceNotBlank_ThenReturnTrue() {

        final boolean isValid = validator.isValid("test", CONTEXT);

        assertThat(isValid).isTrue();
    }
}