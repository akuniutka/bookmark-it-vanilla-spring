package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("UserRemoverImpl Unit Tests")
class UserRemoverImplTest {

    private final UserRemover remover = new UserRemoverImpl();

    @DisplayName("""
            Given a user is null,
            when mark the user as deleted,
            then throw an exception
            """)
    @Test
    void givenUserIsNull_WhenMarkUserAsDeleted_ThenThrowIllegalArgumentException() {

        final Throwable throwable = catchThrowable(() -> remover.markUserAsDeleted(null));

        then(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user is marked non-null but is null");
    }

    @DisplayName("""
            Given a user is deleted,
            when mark the user as deleted,
            then do not change the user's properties, and return false
            """)
    @Test
    void givenUserAlreadyDeleted_WhenMarkUserAsDeleted_ThenReturnFalse() {
        final User user = new User();
        user.setState(User.State.DELETED);

        final boolean hasChanges = remover.markUserAsDeleted(user);

        then(hasChanges).isFalse();
    }

    @DisplayName("""
            Given a user is not deleted,
            when mark the user as deleted,
            then change user's state to DELETE, and return true
            """)
    @Test
    void givenUserNotDeleted_WhenMarkUserAsDeleted_ThenChangeUserStateToDeletedAndReturnTrue() {
        final User user = new User();

        final boolean hasChanges = remover.markUserAsDeleted(user);

        then(hasChanges).isTrue();
        then(user.getState()).isEqualTo(User.State.DELETED);
    }
}
