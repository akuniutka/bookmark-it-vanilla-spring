package io.github.akuniutka.user.mapper;

import io.github.akuniutka.user.dto.CreateUserRequest;
import io.github.akuniutka.user.dto.UpdateUserRequest;
import io.github.akuniutka.user.dto.UserDto;
import io.github.akuniutka.user.entity.User;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public abstract class UserMapper {

    public abstract User mapToEntity(CreateUserRequest request);

    public User mapToEntity(final UUID id, final UpdateUserRequest request) {
        if (id == null || request == null) {
            return null;
        }
        final User user = new User(id);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        switch (request.state()) {
            case ACTIVE -> user.setState(User.State.ACTIVE);
            case BLOCKED -> user.setState(User.State.BLOCKED);
            case null -> user.setState(null);
        }
        return user;
    }

    public abstract UserDto mapToDto(User user);

    public abstract List<UserDto> mapToDto(List<User> users);
}
