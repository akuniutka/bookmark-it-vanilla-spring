package io.github.akuniutka;

import io.github.akuniutka.config.ApplicationConfig;
import io.github.akuniutka.user.controller.UserController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("Application Integration Tests")
@SpringJUnitWebConfig(ApplicationConfig.class)
class BookmarkItApplicationIT {

    @Autowired
    private UserController userController;

    @DisplayName("""
            When load Spring context,
            then no errors occur
            """)
    @Test
    void whenLoadSpringContext_ThenNoErrors() {

        then(userController).isNotNull();
    }
}
