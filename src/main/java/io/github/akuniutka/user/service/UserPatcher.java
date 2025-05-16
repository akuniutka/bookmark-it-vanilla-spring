package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;

public interface UserPatcher {

    /**
     * For each user's property sets its value to that of the relative property in the patch when the value of the
     * property in the patch is not null and differs from the current value of user's property.
     *
     * @param patch the patch to be applied to the user
     * @param user  the user to be updated
     * @return {@code true} if any of user's properties has changed in the result of update, and {@code false} otherwise
     */
    boolean applyPatchToUser(User patch, User user);
}
