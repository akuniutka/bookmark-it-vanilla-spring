package io.github.akuniutka;

import io.github.akuniutka.config.ApplicationConfig;
import io.github.akuniutka.user.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.assertj.core.api.BDDAssertions.then;

@SpringJUnitWebConfig(ApplicationConfig.class)
class BookmarkItApplicationIT {

    @Autowired
    private UserController userController;

    @Test
    void whenLoadSpringContext_ThenNoErrors() {

        then(userController).isNotNull();
    }
}
