package io.github.akuniutka.user;

import io.github.akuniutka.user.dto.UpdateUserRequest;

import static io.github.akuniutka.user.TestUser.OTHER_EMAIL;
import static io.github.akuniutka.user.TestUser.OTHER_FIRST_NAME;
import static io.github.akuniutka.user.TestUser.OTHER_LAST_NAME;

public final class TestUpdateUserRequest {

    public static final UpdateUserRequest.State OTHER_STATE = UpdateUserRequest.State.BLOCKED;

    private TestUpdateUserRequest() {
        throw new AssertionError();
    }

    public static UpdateUserRequest base() {
        return base(OTHER_STATE);
    }

    public static UpdateUserRequest base(final UpdateUserRequest.State state) {
        return UpdateUserRequest.builder()
                .firstName(OTHER_FIRST_NAME)
                .lastName(OTHER_LAST_NAME)
                .email(OTHER_EMAIL)
                .state(state)
                .build();
    }
}
