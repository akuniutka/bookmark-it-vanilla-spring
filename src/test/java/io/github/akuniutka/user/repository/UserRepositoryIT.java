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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitWebConfig(ApplicationConfig.class)
class UserRepositoryIT {

    @Autowired
    private UserRepository repository;

    @Test
    void whenExistsByEmailIgnoreCaseAndUserExist_ThenReturnTrue() {

        final boolean exists = repository.existsByEmailIgnoreCase(EMAIL);

        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByEmailIgnoreCaseAndUserEmailInDifferentCase_ThenReturnTrue() {

        final boolean exists = repository.existsByEmailIgnoreCase(UPPERCASE_EMAIL);

        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByEmailIgnoreCaseAndUserNotExist_ThenReturnFalse() {

        final boolean exists = repository.existsByEmailIgnoreCase(NON_EXISTING_EMAIL);

        assertThat(exists).isFalse();
    }

    @Test
    void whenSaveAndAnotherUserHasSameEmail_ThenThrowDataIntegrityViolationException() {
        final User user = TestUser.patchedWithNewEmailOnly();

        assertThatThrownBy(() -> repository.saveAndFlush(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void whenSaveAndAnotherUserHasSameEmailInDifferentCase_ThenThrowDataIntegrityViolationException() {
        final User user = TestUser.patchedWithNewEmailUppercaseOnly();

        assertThatThrownBy(() -> repository.saveAndFlush(user))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
