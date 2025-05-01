package io.github.akuniutka.user;

import io.github.akuniutka.user.dto.UserDto;
import io.github.akuniutka.user.entity.User;

import static io.github.akuniutka.user.TestUser.EMAIL;
import static io.github.akuniutka.user.TestUser.FIRST_NAME;
import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.user.TestUser.LAST_NAME;
import static io.github.akuniutka.user.TestUser.OTHER_EMAIL;
import static io.github.akuniutka.user.TestUser.OTHER_FIRST_NAME;
import static io.github.akuniutka.user.TestUser.OTHER_LAST_NAME;
import static io.github.akuniutka.user.TestUser.OTHER_STATE;
import static io.github.akuniutka.user.TestUser.REGISTRATION_DATE;
import static io.github.akuniutka.user.TestUser.STATE;

public final class TestUserDto {

    private TestUserDto() {
        throw new AssertionError();
    }

    public static UserDto base() {
        return UserDto.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .state(STATE.name())
                .registrationDate(REGISTRATION_DATE)
                .build();
    }

    public static UserDto patched() {
        return UserDto.builder()
                .id(ID)
                .firstName(OTHER_FIRST_NAME)
                .lastName(OTHER_LAST_NAME)
                .email(OTHER_EMAIL)
                .state(OTHER_STATE.name())
                .registrationDate(REGISTRATION_DATE)
                .build();
    }

    public static UserDto deleted() {
        return UserDto.builder()
                .id(ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .state(User.State.DELETED.name())
                .registrationDate(REGISTRATION_DATE)
                .build();
    }
}
