package io.github.akuniutka.user.service;

import io.github.akuniutka.exception.DuplicateEmailException;
import io.github.akuniutka.exception.UserDeletedException;
import io.github.akuniutka.exception.UserNotFoundException;
import io.github.akuniutka.user.entity.User;
import io.github.akuniutka.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserPatcher patcher;
    private final UserRepository repository;
    private final Clock clock;

    @Override
    public User addUser(@NonNull User user) {
        requireEmailNotYetExist(user.getEmail());
        user.setState(User.State.ACTIVE);
        user.setRegistrationDate(Instant.now(clock));
        user = repository.save(user);
        log.info("New user added: id = {}", user.getId());
        log.debug("User added = {}", user);
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return repository.findAll();
    }

    @Override
    public User getUserById(@NonNull final UUID id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User updateUser(@NonNull final User patch) {
        User user = getUserById(patch.getId());
        requireNotDeleted(user);
        requireEmailNotYetExistOrEquals(patch.getEmail(), user.getEmail());
        boolean hasChanges = patcher.applyPatchToUser(patch, user);
        if (hasChanges) {
            user = repository.save(user);
            log.info("User updated: id = {}", user.getId());
            log.debug("User updated = {}", user);
        } else {
            log.info("No new data for user: id = {}", user.getId());
            log.debug("Data in update = {}", patch);
        }
        return user;
    }

    @Override
    public User deleteUserById(@NonNull final UUID id) {
        User user = getUserById(id);
        user.setState(User.State.DELETED);
        user = repository.save(user);
        log.info("User marked deleted: id = {}", user.getId());
        log.debug("User deleted = {}", user);
        return user;
    }

    private void requireEmailNotYetExist(final String email) {
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }
    }

    private void requireNotDeleted(final User user) {
        if (user.getState() == User.State.DELETED) {
            throw new UserDeletedException(user.getId());
        }
    }

    private void requireEmailNotYetExistOrEquals(final String newEmail, final String oldEmail) {
        if (newEmail == null || newEmail.equalsIgnoreCase(oldEmail)) {
            return;
        }
        requireEmailNotYetExist(newEmail);
    }
}
