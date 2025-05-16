package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;

public interface UserInitializer {

    /**
     * Sets initials values for user's required properties.
     */
    void initUserProperties(User user);
}
