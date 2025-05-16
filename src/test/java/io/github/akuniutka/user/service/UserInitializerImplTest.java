package io.github.akuniutka.user.service;

import io.github.akuniutka.config.ApplicationTestConfig;
import io.github.akuniutka.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("UserInitializerImpl Unit Tests")
class UserInitializerImplTest {

    private static final Clock CLOCK = ApplicationTestConfig.fixedClock();
    private static final Instant NOW = ApplicationTestConfig.FIXED_TIME;

    private final UserInitializer initializer = new UserInitializerImpl(CLOCK);

    @DisplayName("""
            Given a user is null,
            when init users's properties,
            then throw an exception
            """)
    @Test
    void givenUserIsNull_WhenInitUserProperties_ThenThrowIllegalArgumentException() {

        final Throwable throwable = catchThrowable(() -> initializer.initUserProperties(null));

        then(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user is marked non-null but is null");
    }

    @DisplayName("""
            Given a user is not null,
            when init user's properties,
            then set user's state to ACTIVE and registration date to now
            """)
    @Test
    void givenUserNotNull_WhenInitUserProperties_ThenSetUserStateToActiveAndRegistrationDateToNow() {
        final User user = new User();

        initializer.initUserProperties(user);

        then(user.getState()).isEqualTo(User.State.ACTIVE);
        then(user.getRegistrationDate()).isEqualTo(NOW);
    }
}
