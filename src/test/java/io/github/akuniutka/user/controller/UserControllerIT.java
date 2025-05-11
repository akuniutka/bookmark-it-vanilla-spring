package io.github.akuniutka.user.controller;

import io.github.akuniutka.user.TestCreateUserRequest;
import io.github.akuniutka.user.TestUpdateUserRequest;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.TestUserDto;
import io.github.akuniutka.user.mapper.UserMapper;
import io.github.akuniutka.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.util.TestUtils.loadJson;
import static io.github.akuniutka.util.TestUtils.refContains;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;

@DisplayName("UserController Integration Tests")
@ExtendWith(MockitoExtension.class)
class UserControllerIT {

    private static final String BASE_URL = "/users";

    @Mock
    private UserService mockUserService;

    @Mock
    private UserMapper mockUserMapper;

    @InjectMocks
    private UserController userController;

    private MockMvcTester mockMvcTester;

    @BeforeEach
    void setUp() {
        mockMvcTester = MockMvcTester.of(userController);
    }

    @DisplayName("""
            When POST at base URL,
            then create a user, respond with OK and the user created
            """)
    @Test
    void whenPostAtBaseUrl_ThenInvokeCreateUser() throws Exception {
        final String requestBody = loadJson("requests/create_user.json", getClass());
        final String responseBody = loadJson("responses/create_user.json", getClass());
        given(mockUserMapper.mapToEntity(TestCreateUserRequest.base())).willReturn(TestUser.fresh());
        given(mockUserService.addUser(refEq(TestUser.fresh()))).willReturn(TestUser.persisted());
        given(mockUserMapper.mapToDto(refEq(TestUser.persisted()))).willReturn(TestUserDto.base());

        final MvcTestResult response = mockMvcTester
                .post()
                .uri(BASE_URL)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo(responseBody);
    }

    @DisplayName("""
            When GET at base URL,
            then respond with OK and the list of users
            """)
    @Test
    void whenGetAtBaseUrl_ThenInvokeFindAllUsers() throws Exception {
        final String responseBody = loadJson("responses/find_all_users.json", getClass());
        given(mockUserService.findAllUsers()).willReturn(List.of(TestUser.persisted()));
        given(mockUserMapper.mapToDto(refContains(TestUser.persisted()))).willReturn(List.of(TestUserDto.base()));

        final MvcTestResult response = mockMvcTester
                .get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo(responseBody);
    }

    @DisplayName("""
            When GET at base URL with user's ID,
            then respond with OK and the user
            """)
    @Test
    void whenGetAtBaseUrlWithUserId_ThenInvokeGetUserById() throws Exception {
        final String responseBody = loadJson("responses/get_user_by_id.json", getClass());
        given(mockUserService.getUserById(ID)).willReturn(TestUser.persisted());
        given(mockUserMapper.mapToDto(refEq(TestUser.persisted()))).willReturn(TestUserDto.base());

        final MvcTestResult response = mockMvcTester
                .get()
                .uri(BASE_URL + "/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo(responseBody);
    }

    @DisplayName("""
            When PATCH at base URL with user's ID,
            then respond with OK and the user updated
            """)
    @Test
    void whenPatchAtBaseUrlWithUserId_ThenInvokeUpdateUser() throws Exception {
        final String requestBody = loadJson("requests/update_user.json", getClass());
        final String responseBody = loadJson("responses/update_user.json", getClass());
        given(mockUserMapper.mapToEntity(ID, TestUpdateUserRequest.base())).willReturn(TestUser.patch());
        given(mockUserService.updateUser(refEq(TestUser.patch()))).willReturn(TestUser.patched());
        given(mockUserMapper.mapToDto(refEq(TestUser.patched()))).willReturn(TestUserDto.patched());

        final MvcTestResult response = mockMvcTester
                .patch()
                .uri(BASE_URL + "/" + ID)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo(responseBody);
    }

    @DisplayName("""
            When DELETE at base URL with user's ID,
            then respond with OK and the user deleted
            """)
    @Test
    void whenDeleteAtBaseUrlWithUserId_ThenInvokeDeleteUserById() throws Exception {
        final String responseBody = loadJson("responses/delete_user.json", getClass());
        given(mockUserService.deleteUserById(ID)).willReturn(TestUser.deleted());
        given(mockUserMapper.mapToDto(refEq(TestUser.deleted()))).willReturn(TestUserDto.deleted());

        final MvcTestResult response = mockMvcTester
                .delete()
                .uri(BASE_URL + "/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo(responseBody);
    }
}
