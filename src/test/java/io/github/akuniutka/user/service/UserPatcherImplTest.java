package io.github.akuniutka.user.service;

import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
            Given a patch contains new values for all properties and a user is not null,
            when apply the patch to the user,
            then update user's properties and return true
            """)
    @Test
    void givenPatchContainNewValuesForAllProperties_WhenApply_ThenUpdatePropertiesAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patch();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.patched());
    }

    @DisplayName("""
            Given a patch contains old values for all properties and a user is not null,
            when apply the patch to the user,
            then do not change user's properties and return false
            """)
    @Test
    void whenPatchContainOldValuesForAllProperties_WhenApply_ThenDoNotChangePropertiesAndReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithOldValues();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
    }

    @DisplayName("""
            Given a patch contains null for all properties and a user is not null,
            when apply the patch to the user,
            then do not change user's properties and return false
            """)
    @Test
    void whenPatchContainNullForAllProperties_WhenApply_ThenDoNotChangePropertiesAndReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithEmptyFields();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
    }

    @DisplayName("""
            Given a patch contains a new first name only and a user is not null,
            when apply the patch to the user,
            then update user's first name only and return true
            """)
    @Test
    void whenPatchContainNewFirstNameOnly_WhenApply_ThenUpdateFirstNameOnlyAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithNewFirstNameOnly();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithFirstNameOnly());
    }

    @DisplayName("""
            Given a patch contains the old first name only and a user is not null,
            when apply the patch to the user,
            then do not change user's properties and return false
            """)
    @Test
    void whenPatchContainOldFirstNameOnly_WhenApply_ThenDoNotChangePropertiesAndReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithOldFirstNameOnly();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
    }

    @DisplayName("""
            Given a patch contains a new last name only and a user is not null,
            when apply the patch to the user,
            then user's last name only and return true
            """)
    @Test
    void givenPatchContainNewLastNameOnly_WhenApply_ThenUpdateLastNameOnlyAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithNewLastNameOnly();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithLastNameOnly());
    }

    @DisplayName("""
            Given a patch contains an old last name only and a user is not null,
            when apply the patch to the user,
            then do not change user's properties and return false
            """)
    @Test
    void givenPatchContainOldLastNameOnly_WhenApply_ThenDoNotChangePropertiesAndReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithOldLastNameOnly();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
    }

    @DisplayName("""
            Given a patch contains a new email only and a user not is null,
            when apply the patch to the user,
            then update user's email only and return true
            """)
    @Test
    void givenPatchContainNewEmailOnly_WhenApply_ThenUpdateEmailOnlyAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithNewEmailOnly();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithNewEmailOnly());
    }

    @DisplayName("""
            Given a patch contains an old email only and a user is not null,
            when apply the patch to the user,
            then do not change user's properties and return false
            """)
    @Test
    void givenPatchContainOldEmailOnly_WhenApply_ThenDoNotChangePropertiesAndReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithOldEmailOnly();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
    }

    @DisplayName("""
            Given a patch contains a new state only and a user is not null,
            when apply the patch to the user,
            then update user's state and return true
            """)
    @Test
    void givenPatchContainNewStateOnly_WhenApply_ThenUpdateStateOnlyAndReturnTrue() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithNewStateOnly();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isTrue();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithStateOnly());
    }

    @DisplayName("""
            Given a patch contains an old state only and a user is not null,
            when apply the patch to the user,
            then do not change user's properties and return false
            """)
    @Test
    void givenPatchContainOldStateOnly_WhenApply_ThenDoNotChangePropertiesAndReturnFalse() {
        final User user = TestUser.persisted();
        final User patch = TestUser.patchWithOldStateOnly();

        final boolean isUpdated = patcher.applyPatchToUser(patch, user);

        then(isUpdated).isFalse();
        then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
    }
}
