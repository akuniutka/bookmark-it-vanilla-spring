package io.github.akuniutka.user.mapper;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.github.akuniutka.user.TestCreateUserRequest;
import io.github.akuniutka.user.TestUpdateUserRequest;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.TestUserDto;
import io.github.akuniutka.user.dto.UpdateUserRequest;
import io.github.akuniutka.user.dto.UserDto;
import io.github.akuniutka.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static io.github.akuniutka.user.TestUser.ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserMapperImplTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapperImpl();
    }

    @Test
    void whenMapCreateUserRequestToEntityAndRequestIsNull_ThenReturnNull() {

        final User user = mapper.mapToEntity(null);

        assertThat(user).isNull();
    }

    @Test
    void whenMapCreateUserRequestToEntityAndRequestNotNull_ThenReturnCorrectUser() {
        try (MockedStatic<Generators> mockGenerators = Mockito.mockStatic(Generators.class)) {
            final MockedStatic.Verification staticMethod = Generators::timeBasedEpochGenerator;
            final TimeBasedEpochGenerator mockGenerator = Mockito.mock(TimeBasedEpochGenerator.class);
            final InOrder inOrder = Mockito.inOrder(Generators.class, mockGenerator);
            when(Generators.timeBasedEpochGenerator()).thenReturn(mockGenerator);
            when(mockGenerator.generate()).thenReturn(ID);

            final User user = mapper.mapToEntity(TestCreateUserRequest.base());

            assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.fresh());
            inOrder.verify(mockGenerators, staticMethod);
            inOrder.verify(mockGenerator).generate();
        }
    }

    @Test
    void whenMapUpdateUserRequestToEntityAndBothIdAndRequestAreNull_ThenReturnNull() {

        final User patch = mapper.mapToEntity(null, null);

        assertThat(patch).isNull();
    }

    @Test
    void whenMapUpdateUserRequestToEntityAndIdIsNull_ThenReturnNull() {

        final User patch = mapper.mapToEntity(null, TestUpdateUserRequest.base());

        assertThat(patch).isNull();
    }

    @Test
    void whenMapUpdateUserRequestToEntityAndRequestIsNull_ThenReturnNull() {

        final User patch = mapper.mapToEntity(ID, null);

        assertThat(patch).isNull();
    }

    @Test
    void whenMapUpdateUserRequest_ThenReturnCorrectPatch() {

        final User patch = mapper.mapToEntity(ID, TestUpdateUserRequest.base());

        assertThat(patch).usingRecursiveComparison().isEqualTo(TestUser.patch());
    }

    @ParameterizedTest
    @EnumSource(UpdateUserRequest.State.class)
    void whenMapUpdateUserRequest_ThenAllStatesMappedCorrectly(final UpdateUserRequest.State state) {
        final UpdateUserRequest request = TestUpdateUserRequest.base(state);

        final User.State mappedState = mapper.mapToEntity(ID, request).getState();

        assertThat(mappedState.name()).isEqualTo(state.name());
    }

    @Test
    void whenMapUpdateUserRequest_ThenNullStateMappedCorrectly() {
        final UpdateUserRequest request = TestUpdateUserRequest.base(null);

        final User.State mappedState = mapper.mapToEntity(ID, request).getState();

        assertThat(mappedState).isNull();
    }

    @Test
    void whenMapUserToDtoAndUserIsNull_ThenReturnNull() {

        final UserDto dto = mapper.mapToDto((User) null);

        assertThat(dto).isNull();
    }

    @Test
    void whenMapUserToDtoAndUserNotNull_ThenReturnCorrectDto() {

        final UserDto dto = mapper.mapToDto(TestUser.persisted());

        assertThat(dto).isEqualTo(TestUserDto.base());
    }

    @Test
    void whenMapUserToDtoAndStateIsNull_ThenMappedStateIsAlsoNull() {
        final User user = TestUser.persisted();
        user.setState(null);

        final String mappedState = mapper.mapToDto(user).state();

        assertThat(mappedState).isNull();
    }

    @Test
    void whenMapListOfUsersToDtosAndListIsNull_ThenReturnNull() {

        final List<UserDto> dtos = mapper.mapToDto((List<User>) null);

        assertThat(dtos).isNull();
    }

    @Test
    void whenMapListOfUsersToDtosAndListNotNull_ThenReturnCorrectListOfDtos() {

        final List<UserDto> dtos = mapper.mapToDto(List.of(TestUser.persisted()));

        assertThat(dtos).containsExactly(TestUserDto.base());
    }

    @Test
    void whenMpListOfUsersToDtosAndListIsEmpty_ThenReturnEmptyList() {

        final List<UserDto> dtos = mapper.mapToDto(List.of());

        assertThat(dtos).isEmpty();
    }
}
