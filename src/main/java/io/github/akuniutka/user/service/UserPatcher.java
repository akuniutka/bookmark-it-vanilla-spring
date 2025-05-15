package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;

public interface UserPatcher {

    /**
     * For each {@code user}'s property sets its value to that of the relative property in the {@code patch}
     * when the value of the property in the {@code patch} is not null and differs from the current value of
     * {@code user}'s property.
     *
     * @param patch the patch to be applied to the {@code user}
     * @param user  the user to be updated
     * @return {@code true} if any of {@code user}'s properties has changed in the result of update and {@code false}
     * otherwise
     */
    boolean applyPatchToUser(User patch, User user);
}
