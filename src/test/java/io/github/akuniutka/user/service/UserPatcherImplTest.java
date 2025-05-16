package io.github.akuniutka.user.service;

import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.akuniutka.user.TestUser.EMAIL;
import static io.github.akuniutka.user.TestUser.FIRST_NAME;
import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.user.TestUser.LAST_NAME;
import static io.github.akuniutka.user.TestUser.OTHER_EMAIL;
import static io.github.akuniutka.user.TestUser.OTHER_FIRST_NAME;
import static io.github.akuniutka.user.TestUser.OTHER_LAST_NAME;
import static io.github.akuniutka.user.TestUser.OTHER_STATE;
import static io.github.akuniutka.user.TestUser.STATE;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("UserPatcherImpl Unit Tests")
class UserPatcherImplTest {

    private final UserPatcher patcher = new UserPatcherImpl();

    @DisplayName("""
            Given a patch is null,
            when apply the patch to a user,
            then throw an exception
            """)
    @Test
    void givenPatchIsNull_WhenApply_ThenThrowIllegalArgumentException() {

        final Throwable throwable = catchThrowable(() -> patcher.applyPatchToUser(null, TestUser.persisted()));

        then(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("patch is marked non-null but is null");
    }

    @DisplayName("""
            Given a user is null,
            when apply a patch to the user,
            then throw an exception
            """)
    @Test
    void givenUserIsNull_WhenApply_ThenThrowIllegalArgumentException() {

        final Throwable throwable = catchThrowable(() -> patcher.applyPatchToUser(TestUser.patch(), null));

        then(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user is marked non-null but is null");
    }

    @DisplayName("""
            Given a patch contains null for all properties and a user is not null,
            when apply the patch to the user,
            then return false
            """)
    @Test
    void whenPatchContainNullForAllProperties_WhenApply_ThenReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithEmptyFields();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
    }

    @DisplayName("""
            Given a patch contains the old first name and a user is not null,
            when apply the patch to the user,
            then return false
            """)
    @Test
    void whenPatchContainOldFirstName_WhenApply_ThenDoReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = new User(ID);
        patch.setFirstName(FIRST_NAME);

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
    }

    @DisplayName("""
            Given a patch contains a new first name and a user is not null,
            when apply the patch to the user,
            then update user's first name and return true
            """)
    @Test
    void whenPatchContainNewFirstName_WhenApply_ThenUpdateFirstNameAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = new User(ID);
        patch.setFirstName(OTHER_FIRST_NAME);

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user.getFirstName()).isEqualTo(OTHER_FIRST_NAME);
    }

    @DisplayName("""
            Given a patch contains the old last name and a user is not null,
            when apply the patch to the user,
            then return false
            """)
    @Test
    void givenPatchContainOldLastName_WhenApply_ThenReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = new User(ID);
        patch.setLastName(LAST_NAME);

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
    }

    @DisplayName("""
            Given a patch contains a new last name and a user is not null,
            when apply the patch to the user,
            then update user's last name and return true
            """)
    @Test
    void givenPatchContainNewLastName_WhenApply_ThenUpdateLastNameAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = new User(ID);
        patch.setLastName(OTHER_LAST_NAME);

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user.getLastName()).isEqualTo(OTHER_LAST_NAME);
    }

    @DisplayName("""
            Given a patch contains the old email and a user is not null,
            when apply the patch to the user,
            then return false
            """)
    @Test
    void givenPatchContainOldEmail_WhenApply_ThenReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = new User(ID);
        user.setEmail(EMAIL);

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
    }

    @DisplayName("""
            Given a patch contains a new email and a user not is null,
            when apply the patch to the user,
            then update user's email and return true
            """)
    @Test
    void givenPatchContainNewEmail_WhenApply_ThenUpdateEmailAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = new User(ID);
        patch.setEmail(OTHER_EMAIL);

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user.getEmail()).isEqualTo(OTHER_EMAIL);
    }

    @DisplayName("""
            Given a patch contains the old state and a user is not null,
            when apply the patch to the user,
            then return false
            """)
    @Test
    void givenPatchContainOldState_WhenApply_ThenReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = new User(ID);
        patch.setState(STATE);

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
    }

    @DisplayName("""
            Given a patch contains a new state and a user is not null,
            when apply the patch to the user,
            then update user's state and return true
            """)
    @Test
    void givenPatchContainNewState_WhenApply_ThenUpdateStateAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = new User(ID);
        patch.setState(OTHER_STATE);

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user.getState()).isEqualTo(OTHER_STATE);
    }
}
