package io.github.akuniutka.user.repository;

import io.github.akuniutka.config.ApplicationConfig;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static io.github.akuniutka.user.TestUser.EMAIL;
import static io.github.akuniutka.user.TestUser.NON_EXISTING_EMAIL;
import static io.github.akuniutka.user.TestUser.UPPERCASE_EMAIL;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@SpringJUnitWebConfig(ApplicationConfig.class)
class UserRepositoryIT {

    @Autowired
    private UserRepository repository;

    @Test
    void whenExistsByEmailIgnoreCaseAndUserExist_ThenReturnTrue() {

        final boolean exists = repository.existsByEmailIgnoreCase(EMAIL);

        then(exists).isTrue();
    }

    @Test
    void whenExistsByEmailIgnoreCaseAndUserEmailInDifferentCase_ThenReturnTrue() {

        final boolean exists = repository.existsByEmailIgnoreCase(UPPERCASE_EMAIL);

        then(exists).isTrue();
    }

    @Test
    void whenExistsByEmailIgnoreCaseAndUserNotExist_ThenReturnFalse() {

        final boolean exists = repository.existsByEmailIgnoreCase(NON_EXISTING_EMAIL);

        then(exists).isFalse();
    }

    @Test
    void whenSaveAndAnotherUserHasSameEmail_ThenThrowDataIntegrityViolationException() {
        final User user = TestUser.patchedWithNewEmailOnly();

        final Throwable throwable = catchThrowable(() -> repository.saveAndFlush(user));

        then(throwable).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void whenSaveAndAnotherUserHasSameEmailInDifferentCase_ThenThrowDataIntegrityViolationException() {
        final User user = TestUser.patchedWithNewEmailUppercaseOnly();

        final Throwable throwable = catchThrowable(() -> repository.saveAndFlush(user));

        then(throwable).isInstanceOf(DataIntegrityViolationException.class);
    }
}
