package io.github.akuniutka.user.service;

import io.github.akuniutka.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User addUser(User user);

    List<User> findAllUsers();

    User getUserById(UUID id);

    User updateUser(User patch);

    User deleteUserById(UUID id);
}
