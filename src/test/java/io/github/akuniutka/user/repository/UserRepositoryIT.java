package io.github.akuniutka.user.repository;

import io.github.akuniutka.config.ApplicationConfig;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static io.github.akuniutka.user.TestUser.EMAIL;
import static io.github.akuniutka.user.TestUser.NON_EXISTING_EMAIL;
import static io.github.akuniutka.user.TestUser.UPPERCASE_EMAIL;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("UserRepository Integration Tests")
@SpringJUnitWebConfig(ApplicationConfig.class)
class UserRepositoryIT {

    @Autowired
    private UserRepository repository;

    @DisplayName("""
            Given a user exists,
            when check if the user exists by their email,
            then return true
            """)
    @Test
    void givenUserExist_WhenExistsByEmailIgnoreCase_ThenReturnTrue() {

        final boolean exists = repository.existsByEmailIgnoreCase(EMAIL);

        then(exists).isTrue();
    }

    @DisplayName("""
            Given user's email is in different case,
            when check if the user exists by their email,
            then return true
            """)
    @Test
    void givenUserEmailIsInDifferentCase_WhenExistsByEmailIgnoreCase_ThenReturnTrue() {

        final boolean exists = repository.existsByEmailIgnoreCase(UPPERCASE_EMAIL);

        then(exists).isTrue();
    }

    @DisplayName("""
            Given no user exists with the email specified,
            when chek if a user exists by that email,
            then return false
            """)
    @Test
    void givenNoUserExist_WhenExistsByEmailIgnoreCase_ThenReturnFalse() {

        final boolean exists = repository.existsByEmailIgnoreCase(NON_EXISTING_EMAIL);

        then(exists).isFalse();
    }

    @DisplayName("""
            Given another user exists with the email specified,
            when save a user with the same email,
            then throw an exception
            """)
    @Test
    void givenAnotherUserHasSameEmail_WhenSave_ThenThrowDataIntegrityViolationException() {
        final User user = TestUser.patchedWithNewEmailOnly();

        final Throwable throwable = catchThrowable(() -> repository.saveAndFlush(user));

        then(throwable).isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("""
            Given another user has the same email in different case,
            when save a user with the same email in different case,
            then throw an exception
            """)
    @Test
    void givenAnotherUserHasSameEmailInDifferentCase_WhenSave_ThenThrowDataIntegrityViolationException() {
        final User user = TestUser.patchedWithNewEmailUppercaseOnly();

        final Throwable throwable = catchThrowable(() -> repository.saveAndFlush(user));

        then(throwable).isInstanceOf(DataIntegrityViolationException.class);
    }
}
