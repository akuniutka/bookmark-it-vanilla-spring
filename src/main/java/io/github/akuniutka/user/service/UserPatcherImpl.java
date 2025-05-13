package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserPatcherImpl implements UserPatcher {

    @Override
    public boolean applyPatchToUser(@NonNull final User patch, @NonNull final User user) {
        boolean hasChanges = false;
        if (patch.getFirstName() != null && !patch.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(patch.getFirstName());
            hasChanges = true;
        }
        if (patch.getLastName() != null && !patch.getLastName().equals(user.getLastName())) {
            user.setLastName(patch.getLastName());
            hasChanges = true;
        }
        if (patch.getEmail() != null && !patch.getEmail().equals(user.getEmail())) {
            user.setEmail(patch.getEmail());
            hasChanges = true;
        }
        if (patch.getState() != null && !patch.getState().equals(user.getState())) {
            user.setState(patch.getState());
            hasChanges = true;
        }
        return hasChanges;
    }
}
