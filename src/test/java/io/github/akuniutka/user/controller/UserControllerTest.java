package io.github.akuniutka.user.controller;

import io.github.akuniutka.exception.DtoNotValidException;
import io.github.akuniutka.user.TestCreateUserRequest;
import io.github.akuniutka.user.TestUpdateUserRequest;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.TestUserDto;
import io.github.akuniutka.user.dto.CreateUserRequest;
import io.github.akuniutka.user.dto.UpdateUserRequest;
import io.github.akuniutka.user.dto.UserDto;
import io.github.akuniutka.user.entity.User;
import io.github.akuniutka.user.mapper.UserMapper;
import io.github.akuniutka.user.service.UserService;
import io.github.akuniutka.util.LogListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.List;

import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.util.TestUtils.assertLogs;
import static io.github.akuniutka.util.TestUtils.refContains;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private InOrder inOrder;

    @BeforeEach
    void setUp() {
        inOrder = Mockito.inOrder(mockUserService, mockUserMapper, mockBindingResult);
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockUserService, mockUserMapper, mockBindingResult);
    }

    @Test
    void whenCreateUserAndBindingResultHasErrors_ThenThrowDtoNotValidException() {
        final CreateUserRequest request = TestCreateUserRequest.base();
        when(mockBindingResult.hasErrors()).thenReturn(true);

        assertThatThrownBy(() -> controller.createUser(request, mockBindingResult))
                .isInstanceOf(DtoNotValidException.class)
                .hasFieldOrPropertyWithValue("errors", mockBindingResult);
        verify(mockBindingResult).hasErrors();
    }

    @Test
    void whenCreateUserAndBindingResultHasNoErrors_ThenMapToEntityAndPassToServiceAndMapResultToDtoAndReturnDtoAndLog()
            throws Exception {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockUserMapper.mapToEntity(any())).thenReturn(TestUser.fresh());
        when(mockUserService.addUser(any())).thenReturn(TestUser.persisted());
        when(mockUserMapper.mapToDto(any(User.class))).thenReturn(TestUserDto.base());

        final UserDto dto = controller.createUser(TestCreateUserRequest.base(), mockBindingResult);

        assertThat(dto).isEqualTo(TestUserDto.base());
        assertLogs(logListener.getEvents(), "create_user.json", getClass());
        inOrder.verify(mockBindingResult).hasErrors();
        inOrder.verify(mockUserMapper).mapToEntity(TestCreateUserRequest.base());
        inOrder.verify(mockUserService).addUser(refEq(TestUser.fresh()));
        inOrder.verify(mockUserMapper).mapToDto(refEq(TestUser.persisted()));
    }

    @Test
    void whenFindAllUsers_ThenGetFromServiceListOfUsersAndMapThemToDtosAndReturnAndLog() throws Exception {
        when(mockUserService.findAllUsers()).thenReturn(List.of(TestUser.persisted()));
        when(mockUserMapper.mapToDto(anyList())).thenReturn(List.of(TestUserDto.base()));

        final List<UserDto> dtos = controller.findAllUsers();

        assertThat(dtos).containsExactly(TestUserDto.base());
        assertLogs(logListener.getEvents(), "find_all_users.json", getClass());
        inOrder.verify(mockUserService).findAllUsers();
        inOrder.verify(mockUserMapper).mapToDto(refContains(TestUser.persisted()));
    }

    @Test
    void whenGetUserById_ThenPassUserIdToServiceAndMapResultToDtoAndReturnDtoAndLog() throws Exception {
        when(mockUserService.getUserById(any())).thenReturn(TestUser.persisted());
        when(mockUserMapper.mapToDto(any(User.class))).thenReturn(TestUserDto.base());

        final UserDto dto = controller.getUserById(ID);

        assertThat(dto).isEqualTo(TestUserDto.base());
        assertLogs(logListener.getEvents(), "get_user_by_id.json", getClass());
        inOrder.verify(mockUserService).getUserById(ID);
        inOrder.verify(mockUserMapper).mapToDto(refEq(TestUser.persisted()));
    }

    @Test
    void whenUpdateUserAndBindingResultHasErrors_ThenThrowDtoNotValidException() {
        final UpdateUserRequest request = TestUpdateUserRequest.base();
        when(mockBindingResult.hasErrors()).thenReturn(true);

        assertThatThrownBy(() -> controller.updateUser(ID, request, mockBindingResult))
                .isInstanceOf(DtoNotValidException.class)
                .hasFieldOrPropertyWithValue("errors", mockBindingResult);
        verify(mockBindingResult).hasErrors();
    }

    @Test
    void whenUpdateUserAndBindingResultHasNoErrors_ThenMapToEntityAndPassToServiceAndMapResultToDtoAndReturnDtoAndLog()
            throws Exception {
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockUserMapper.mapToEntity(any(), any())).thenReturn(TestUser.patch());
        when(mockUserService.updateUser(any())).thenReturn(TestUser.patched());
        when(mockUserMapper.mapToDto(any(User.class))).thenReturn(TestUserDto.patched());

        final UserDto dto = controller.updateUser(ID, TestUpdateUserRequest.base(), mockBindingResult);

        assertThat(dto).isEqualTo(TestUserDto.patched());
        assertLogs(logListener.getEvents(), "update_user.json", getClass());
        inOrder.verify(mockBindingResult).hasErrors();
        inOrder.verify(mockUserMapper).mapToEntity(ID, TestUpdateUserRequest.base());
        inOrder.verify(mockUserService).updateUser(refEq(TestUser.patch()));
        inOrder.verify(mockUserMapper).mapToDto(refEq(TestUser.patched()));
    }

    @Test
    void whenDeleteUserById_ThenPassUserIdToServiceAndMapResultToDtoAndReturnDtoAndLog() throws Exception {
        when(mockUserService.deleteUserById(any())).thenReturn(TestUser.deleted());
        when(mockUserMapper.mapToDto(any(User.class))).thenReturn(TestUserDto.deleted());

        final UserDto dto = controller.deleteUserById(ID);

        assertThat(dto).isEqualTo(TestUserDto.deleted());
        assertLogs(logListener.getEvents(), "delete_user_by_id.json", getClass());
        inOrder.verify(mockUserService).deleteUserById(ID);
        inOrder.verify(mockUserMapper).mapToDto(refEq(TestUser.deleted()));
    }
}
