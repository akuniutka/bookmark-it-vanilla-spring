package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserRemoverImpl implements UserRemover {

    @Override
    public boolean markUserAsDeleted(@NonNull final User user) {
        if (user.getState() == User.State.DELETED) {
            return false;
        }
        user.setState(User.State.DELETED);
        return true;
    }
}
