package io.github.akuniutka.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final UUID userId;

    public UserNotFoundException(final UUID userId) {
        super("User %s does not exist".formatted(userId));
        this.userId = userId;
    }
}
