package io.github.akuniutka.exception;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class DtoNotValidException extends RuntimeException {

    private final transient Errors errors;

    public DtoNotValidException(final Errors errors) {
        super("DTO validation error");
        this.errors = errors;
    }
}
