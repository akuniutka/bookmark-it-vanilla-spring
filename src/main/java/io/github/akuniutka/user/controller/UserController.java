package io.github.akuniutka.user.controller;

import io.github.akuniutka.exception.DtoNotValidException;
import io.github.akuniutka.user.mapper.UserMapper;
import io.github.akuniutka.user.service.UserService;
import io.github.akuniutka.user.dto.CreateUserRequest;
import io.github.akuniutka.user.dto.UpdateUserRequest;
import io.github.akuniutka.user.dto.UserDto;
import io.github.akuniutka.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody final CreateUserRequest request, final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new DtoNotValidException(bindingResult);
        }
        log.info("Received request to create user: email = {}", request.email());
        log.debug("Create user request = {}", request);
        User user = userMapper.mapToEntity(request);
        user = userService.addUser(user);
        final UserDto dto = userMapper.mapToDto(user);
        log.info("Responded with user created: id = {}, email = {}", dto.id(), dto.email());
        log.debug("User created = {}", dto);
        return dto;
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Received request for users");
        final List<User> users = userService.findAllUsers();
        final List<UserDto> dtos = userMapper.mapToDto(users);
        log.info("Responded with users requested");
        log.debug("Users requested = {}", dtos);
        return dtos;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") final UUID id) {
        log.info("Received request for user: id = {}", id);
        final User user = userService.getUserById(id);
        final UserDto dto = userMapper.mapToDto(user);
        log.info("Responded with user requested: id = {}", dto.id());
        log.debug("User requested = {}", dto);
        return dto;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") final UUID id, @Valid @RequestBody final UpdateUserRequest request,
            final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new DtoNotValidException(bindingResult);
        }
        log.info("Received request to update user: id = {}", id);
        log.debug("Update user request = {}", request);
        final User patch = userMapper.mapToEntity(id, request);
        final User user = userService.updateUser(patch);
        final UserDto dto = userMapper.mapToDto(user);
        log.info("Responded with user updated: id = {}", dto.id());
        log.debug("User updated = {}", dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUserById(@PathVariable("id") final UUID id) {
        log.info("Received request to delete user: id = {}", id);
        final User user = userService.deleteUserById(id);
        final UserDto dto = userMapper.mapToDto(user);
        log.info("Responded with user deleted: id = {}", dto.id());
        log.debug("User deleted = {}", dto);
        return dto;
    }
}
