package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;

public interface UserPatcher {

    boolean applyPatchToUser(User patch, User user);
}
