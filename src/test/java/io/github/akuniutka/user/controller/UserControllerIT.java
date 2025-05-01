package io.github.akuniutka.user.controller;

import io.github.akuniutka.user.TestCreateUserRequest;
import io.github.akuniutka.user.TestUpdateUserRequest;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.TestUserDto;
import io.github.akuniutka.user.entity.User;
import io.github.akuniutka.user.mapper.UserMapper;
import io.github.akuniutka.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.github.akuniutka.util.TestUtils.loadJson;
import static io.github.akuniutka.util.TestUtils.refContains;
import static io.github.akuniutka.user.TestUser.ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIT {

    private static final String BASE_URL = "/users";

    private UserService mockUserService;
    private UserMapper mockUserMapper;
    private MockMvc mockMvc;
    private InOrder inOrder;

    @BeforeEach
    void setUp() {
        mockUserService = Mockito.mock(UserService.class);
        mockUserMapper = Mockito.mock(UserMapper.class);
        inOrder = Mockito.inOrder(mockUserService, mockUserMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(mockUserService, mockUserMapper)).build();
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockUserService, mockUserMapper);
    }

    @Test
    void whenPostAtBaseUrl_ThenInvokeCreateUser() throws Exception {
        final String requestBody = loadJson("requests/create_user.json", getClass());
        final String responseBody = loadJson("responses/create_user.json", getClass());
        when(mockUserMapper.mapToEntity(any())).thenReturn(TestUser.fresh());
        when(mockUserService.addUser(any())).thenReturn(TestUser.persisted());
        when(mockUserMapper.mapToDto(any(User.class))).thenReturn(TestUserDto.base());

        mockMvc.perform(post(BASE_URL)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockUserMapper).mapToEntity(TestCreateUserRequest.base());
        inOrder.verify(mockUserService).addUser(refEq(TestUser.fresh()));
        inOrder.verify(mockUserMapper).mapToDto(refEq(TestUser.persisted()));
    }

    @Test
    void whenGetAtBaseUrl_ThenInvokeFindAllUsers() throws Exception {
        final String responseBody = loadJson("responses/find_all_users.json", getClass());
        when(mockUserService.findAllUsers()).thenReturn(List.of(TestUser.persisted()));
        when(mockUserMapper.mapToDto(anyList())).thenReturn(List.of(TestUserDto.base()));

        mockMvc.perform(get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockUserService).findAllUsers();
        inOrder.verify(mockUserMapper).mapToDto(refContains(TestUser.persisted()));
    }

    @Test
    void whenGetAtBaseUrlWithUserId_ThenInvokeGetUserById() throws Exception {
        final String responseBody = loadJson("responses/get_user_by_id.json", getClass());
        when(mockUserService.getUserById(any())).thenReturn(TestUser.persisted());
        when(mockUserMapper.mapToDto(any(User.class))).thenReturn(TestUserDto.base());

        mockMvc.perform(get(BASE_URL + "/" + ID)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        verify(mockUserService).getUserById(ID);
        verify(mockUserMapper).mapToDto(refEq(TestUser.persisted()));
    }

    @Test
    void whenPatchAtBaseUrlWithUserId_ThenInvokeUpdateUser() throws Exception {
        final String requestBody = loadJson("requests/update_user.json", getClass());
        final String responseBody = loadJson("responses/update_user.json", getClass());
        when(mockUserMapper.mapToEntity(any(), any())).thenReturn(TestUser.patch());
        when(mockUserService.updateUser(any())).thenReturn(TestUser.patched());
        when(mockUserMapper.mapToDto(any(User.class))).thenReturn(TestUserDto.patched());

        mockMvc.perform(patch(BASE_URL + "/" + ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockUserMapper).mapToEntity(ID, TestUpdateUserRequest.base());
        inOrder.verify(mockUserService).updateUser(refEq(TestUser.patch()));
        inOrder.verify(mockUserMapper).mapToDto(refEq(TestUser.patched()));
    }

    @Test
    void whenDeleteAtBaseUrlWithUserId_ThenInvokeDeleteUserById() throws Exception {
        final String responseBody = loadJson("responses/delete_user.json", getClass());
        when(mockUserService.deleteUserById(any())).thenReturn(TestUser.deleted());
        when(mockUserMapper.mapToDto(any(User.class))).thenReturn(TestUserDto.deleted());

        mockMvc.perform(delete(BASE_URL + "/" + ID)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        verify(mockUserService).deleteUserById(ID);
        verify(mockUserMapper).mapToDto(refEq(TestUser.deleted()));
    }
}
