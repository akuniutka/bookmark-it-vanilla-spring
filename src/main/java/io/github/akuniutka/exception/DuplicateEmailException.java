package io.github.akuniutka.exception;

import lombok.Getter;

@Getter
public class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(final String email) {
        super("User with email %s already registered".formatted(email));
        this.email = email;
    }
}
