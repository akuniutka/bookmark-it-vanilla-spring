package io.github.akuniutka.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserDeletedException extends RuntimeException {

    private final UUID userId;

    public UserDeletedException(final UUID userId) {
        super("Cannot update user %s: user deleted".formatted(userId));
        this.userId = userId;
    }
}
