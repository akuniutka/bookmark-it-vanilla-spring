package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserInitializerImpl implements UserInitializer {

    private final Clock clock;

    @Override
    public void initUserProperties(@NonNull final User user) {
        user.setState(User.State.ACTIVE);
        user.setRegistrationDate(Instant.now(clock));
    }
}
