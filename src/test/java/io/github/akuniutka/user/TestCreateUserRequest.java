package io.github.akuniutka.user;

import io.github.akuniutka.user.dto.CreateUserRequest;

import static io.github.akuniutka.user.TestUser.EMAIL;
import static io.github.akuniutka.user.TestUser.FIRST_NAME;
import static io.github.akuniutka.user.TestUser.LAST_NAME;

public final class TestCreateUserRequest {

    private TestCreateUserRequest() {
        throw new AssertionError();
    }

    public static CreateUserRequest base() {
        return CreateUserRequest.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .build();
    }
}
