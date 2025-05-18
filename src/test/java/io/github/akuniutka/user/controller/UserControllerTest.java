package io.github.akuniutka.user.controller;

import io.github.akuniutka.exception.DtoNotValidException;
import io.github.akuniutka.log.InjectLogCaptor;
import io.github.akuniutka.log.LogCaptor;
import io.github.akuniutka.log.LogEvents;
import io.github.akuniutka.log.WithLogCapture;
import io.github.akuniutka.user.TestCreateUserRequest;
import io.github.akuniutka.user.TestUpdateUserRequest;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.TestUserDto;
import io.github.akuniutka.user.dto.CreateUserRequest;
import io.github.akuniutka.user.dto.UpdateUserRequest;
import io.github.akuniutka.user.dto.UserDto;
import io.github.akuniutka.user.mapper.UserMapper;
import io.github.akuniutka.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.List;

import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.util.TestUtils.refContains;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;

@DisplayName("UserController Unit Tests")
@ExtendWith(MockitoExtension.class)
@WithLogCapture(UserController.class)
class UserControllerTest {

    @InjectLogCaptor
    LogCaptor logCaptor;

    @Mock
    private UserService mockUserService;

    @Mock
    private UserMapper mockUserMapper;

    @Mock
    private BindingResult mockBindingResult;

    @InjectMocks
    private UserController controller;

    @DisplayName("""
            Given a binding result has errors,
            when create a user,
            then throw an exception
            """)
    @Test
    void givenBindingResultHasErrors_WhenCreateUser_ThenThrowDtoNotValidException() {
        final CreateUserRequest request = TestCreateUserRequest.base();
        given(mockBindingResult.hasErrors()).willReturn(true);

        final Throwable throwable = catchThrowable(() -> controller.createUser(request, mockBindingResult));

        then(throwable)
                .isInstanceOf(DtoNotValidException.class)
                .hasFieldOrPropertyWithValue("errors", mockBindingResult);
    }

    @DisplayName("""
            Given a binding result has no errors,
            when create a user,
            then pass the user to the service, return service's response, log the request and the response
            """)
    @Test
    void givenBindingResultHasNoErrors_WhenCreateUser_ThenMapToEntityAndPassToServiceAndMapResultAndReturnDtoAndLog() {
        given(mockUserMapper.mapToEntity(TestCreateUserRequest.base())).willReturn(TestUser.fresh());
        given(mockUserService.addUser(refEq(TestUser.fresh()))).willReturn(TestUser.persisted());
        given(mockUserMapper.mapToDto(refEq(TestUser.persisted()))).willReturn(TestUserDto.base());

        final UserDto dto = controller.createUser(TestCreateUserRequest.base(), mockBindingResult);

        then(dto).isEqualTo(TestUserDto.base());
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "INFO", "Received request to create user: email = john@mail.com",
                "INFO", "Responded with user created: id = 92f08b0a-4302-40ff-823d-b9ce18522552, email = john@mail.com"
        ));
    }

    @DisplayName("""
            When find all users,
            then return service's response, log the request and the response
            """)
    @Test
    void whenFindAllUsers_ThenGetFromServiceListOfUsersAndMapThemToDtosAndReturnAndLog() {
        given(mockUserService.findAllUsers()).willReturn(List.of(TestUser.persisted()));
        given(mockUserMapper.mapToDto(refContains(TestUser.persisted()))).willReturn(List.of(TestUserDto.base()));

        final List<UserDto> dtos = controller.findAllUsers();

        then(dtos).containsExactly(TestUserDto.base());
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "INFO", "Received request for users",
                "INFO", "Responded with users requested"
        ));
    }

    @DisplayName("""
            When get a user by their ID,
            then pass ID to the service, return service's response, log the request and the response
            """)
    @Test
    void whenGetUserById_ThenPassUserIdToServiceAndMapResultToDtoAndReturnDtoAndLog() {
        given(mockUserService.getUserById(ID)).willReturn(TestUser.persisted());
        given(mockUserMapper.mapToDto(refEq(TestUser.persisted()))).willReturn(TestUserDto.base());

        final UserDto dto = controller.getUserById(ID);

        then(dto).isEqualTo(TestUserDto.base());
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "INFO", "Received request for user: id = 92f08b0a-4302-40ff-823d-b9ce18522552",
                "INFO", "Responded with user requested: id = 92f08b0a-4302-40ff-823d-b9ce18522552"
        ));
    }

    @DisplayName("""
            Given a binding result has errors,
            when update a user,
            then throw an exception
            """)
    @Test
    void givenBindingResultHasErrors_WhenUpdateUser_ThenThrowDtoNotValidException() {
        final UpdateUserRequest request = TestUpdateUserRequest.base();
        given(mockBindingResult.hasErrors()).willReturn(true);

        final Throwable throwable = catchThrowable(() -> controller.updateUser(ID, request, mockBindingResult));

        then(throwable)
                .isInstanceOf(DtoNotValidException.class)
                .hasFieldOrPropertyWithValue("errors", mockBindingResult);
    }

    @DisplayName("""
            Given a binding result has no errors,
            when update a user,
            then pass the user to the service, return service's response, log the request and the response
            """)
    @Test
    void givenBindingResultHasNoErrors_WhenUpdateUser_ThenMapToEntityAndPassToServiceAndMapResultAndReturnDtoAndLog() {
        given(mockUserMapper.mapToEntity(ID, TestUpdateUserRequest.base())).willReturn(TestUser.patch());
        given(mockUserService.updateUser(refEq(TestUser.patch()))).willReturn(TestUser.patched());
        given(mockUserMapper.mapToDto(refEq(TestUser.patched()))).willReturn(TestUserDto.patched());

        final UserDto dto = controller.updateUser(ID, TestUpdateUserRequest.base(), mockBindingResult);

        then(dto).isEqualTo(TestUserDto.patched());
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "INFO", "Received request to update user: id = 92f08b0a-4302-40ff-823d-b9ce18522552",
                "INFO", "Responded with user updated: id = 92f08b0a-4302-40ff-823d-b9ce18522552"
        ));
    }

    @DisplayName("""
            When delete a user by their ID,
            then pass ID to the service, return service's response, log the request and the response
            """)
    @Test
    void whenDeleteUserById_ThenPassUserIdToServiceAndMapResultToDtoAndReturnDtoAndLog() {
        given(mockUserService.deleteUserById(ID)).willReturn(TestUser.deleted());
        given(mockUserMapper.mapToDto(refEq(TestUser.deleted()))).willReturn(TestUserDto.deleted());

        final UserDto dto = controller.deleteUserById(ID);

        then(dto).isEqualTo(TestUserDto.deleted());
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "INFO", "Received request to delete user: id = 92f08b0a-4302-40ff-823d-b9ce18522552",
                "INFO", "Responded with user deleted: id = 92f08b0a-4302-40ff-823d-b9ce18522552"
        ));
    }
}
