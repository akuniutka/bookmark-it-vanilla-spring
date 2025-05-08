package io.github.akuniutka.user.controller;

import io.github.akuniutka.exception.DtoNotValidException;
import io.github.akuniutka.user.TestCreateUserRequest;
import io.github.akuniutka.user.TestUpdateUserRequest;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.TestUserDto;
import io.github.akuniutka.user.dto.CreateUserRequest;
import io.github.akuniutka.user.dto.UpdateUserRequest;
import io.github.akuniutka.user.dto.UserDto;
import io.github.akuniutka.user.mapper.UserMapper;
import io.github.akuniutka.user.service.UserService;
import io.github.akuniutka.util.LogListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.List;

import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.util.TestUtils.assertLogs;
import static io.github.akuniutka.util.TestUtils.refContains;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final LogListener logListener = new LogListener(UserController.class);

    @Mock
    private UserService mockUserService;

    @Mock
    private UserMapper mockUserMapper;

    @Mock
    private BindingResult mockBindingResult;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
    }

    @Test
    void whenCreateUserAndBindingResultHasErrors_ThenThrowDtoNotValidException() {
        final CreateUserRequest request = TestCreateUserRequest.base();
        given(mockBindingResult.hasErrors()).willReturn(true);

        final Throwable throwable = catchThrowable(() -> controller.createUser(request, mockBindingResult));

        then(throwable)
                .isInstanceOf(DtoNotValidException.class)
                .hasFieldOrPropertyWithValue("errors", mockBindingResult);
    }

    @Test
    void whenCreateUserAndBindingResultHasNoErrors_ThenMapToEntityAndPassToServiceAndMapResultToDtoAndReturnDtoAndLog()
            throws Exception {
        given(mockUserMapper.mapToEntity(TestCreateUserRequest.base())).willReturn(TestUser.fresh());
        given(mockUserService.addUser(refEq(TestUser.fresh()))).willReturn(TestUser.persisted());
        given(mockUserMapper.mapToDto(refEq(TestUser.persisted()))).willReturn(TestUserDto.base());

        final UserDto dto = controller.createUser(TestCreateUserRequest.base(), mockBindingResult);

        then(dto).isEqualTo(TestUserDto.base());
        assertLogs(logListener.getEvents(), "create_user.json", getClass());
    }

    @Test
    void whenFindAllUsers_ThenGetFromServiceListOfUsersAndMapThemToDtosAndReturnAndLog() throws Exception {
        given(mockUserService.findAllUsers()).willReturn(List.of(TestUser.persisted()));
        given(mockUserMapper.mapToDto(refContains(TestUser.persisted()))).willReturn(List.of(TestUserDto.base()));

        final List<UserDto> dtos = controller.findAllUsers();

        then(dtos).containsExactly(TestUserDto.base());
        assertLogs(logListener.getEvents(), "find_all_users.json", getClass());
    }

    @Test
    void whenGetUserById_ThenPassUserIdToServiceAndMapResultToDtoAndReturnDtoAndLog() throws Exception {
        given(mockUserService.getUserById(ID)).willReturn(TestUser.persisted());
        given(mockUserMapper.mapToDto(refEq(TestUser.persisted()))).willReturn(TestUserDto.base());

        final UserDto dto = controller.getUserById(ID);

        then(dto).isEqualTo(TestUserDto.base());
        assertLogs(logListener.getEvents(), "get_user_by_id.json", getClass());
    }

    @Test
    void whenUpdateUserAndBindingResultHasErrors_ThenThrowDtoNotValidException() {
        final UpdateUserRequest request = TestUpdateUserRequest.base();
        given(mockBindingResult.hasErrors()).willReturn(true);

        final Throwable throwable = catchThrowable(() -> controller.updateUser(ID, request, mockBindingResult));

        then(throwable)
                .isInstanceOf(DtoNotValidException.class)
                .hasFieldOrPropertyWithValue("errors", mockBindingResult);
    }

    @Test
    void whenUpdateUserAndBindingResultHasNoErrors_ThenMapToEntityAndPassToServiceAndMapResultToDtoAndReturnDtoAndLog()
            throws Exception {
        given(mockUserMapper.mapToEntity(ID, TestUpdateUserRequest.base())).willReturn(TestUser.patch());
        given(mockUserService.updateUser(refEq(TestUser.patch()))).willReturn(TestUser.patched());
        given(mockUserMapper.mapToDto(refEq(TestUser.patched()))).willReturn(TestUserDto.patched());

        final UserDto dto = controller.updateUser(ID, TestUpdateUserRequest.base(), mockBindingResult);

        then(dto).isEqualTo(TestUserDto.patched());
        assertLogs(logListener.getEvents(), "update_user.json", getClass());
    }

    @Test
    void whenDeleteUserById_ThenPassUserIdToServiceAndMapResultToDtoAndReturnDtoAndLog() throws Exception {
        given(mockUserService.deleteUserById(ID)).willReturn(TestUser.deleted());
        given(mockUserMapper.mapToDto(refEq(TestUser.deleted()))).willReturn(TestUserDto.deleted());

        final UserDto dto = controller.deleteUserById(ID);

        then(dto).isEqualTo(TestUserDto.deleted());
        assertLogs(logListener.getEvents(), "delete_user_by_id.json", getClass());
    }
}
