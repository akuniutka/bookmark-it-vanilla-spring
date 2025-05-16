package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;

public interface UserRemover {

    /**
     * Marks the user as deleted.
     *
     * @param user the user to be deleted
     * @return {@code true} if the user has not been marked as deleted before, and {@code false} otherwise
     */
    boolean markUserAsDeleted(User user);
}
