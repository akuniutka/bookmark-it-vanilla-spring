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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static io.github.akuniutka.user.TestUser.ID;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.when;

@DisplayName("UserMapperImpl Unit Tests")
class UserMapperImplTest {

    private final UserMapper mapper = new UserMapperImpl();

    @DisplayName("Map CreateUserRequest to User")
    @Nested
    class MapCreateUserRequestToEntityTest {

        @DisplayName("""
                Given a request is null,
                when map the request to a user,
                then return null
                """)
        @Test
        void whenMapCreateUserRequestToEntityAndRequestIsNull_ThenReturnNull() {

            final User user = mapper.mapToEntity(null);

            then(user).isNull();
        }

        @DisplayName("""
                Given a request is not null,
                when map the request to a user,
                then return a correct user
                """)
        @Test
        void whenMapCreateUserRequestToEntityAndRequestNotNull_ThenReturnCorrectUser() {
            try (MockUUIDGenerator ignored = new MockUUIDGenerator()) {

                final User user = mapper.mapToEntity(TestCreateUserRequest.base());

                then(user).usingRecursiveComparison().isEqualTo(TestUser.fresh());
            }
        }
    }

    @DisplayName("Map UpdateUserRequest to User")
    @Nested
    class MapUpdateUserRequestToEntityTest {

        @DisplayName("""
                Given user's ID is null and a request is null,
                when map ID and the request to a user,
                then return null
                """)
        @Test
        void whenMapUpdateUserRequestToEntityAndBothIdAndRequestAreNull_ThenReturnNull() {

            final User patch = mapper.mapToEntity(null, null);

            then(patch).isNull();
        }

        @DisplayName("""
                Given user's ID is null and a request is not null,
                when map ID and the request to a user,
                then return null
                """)
        @Test
        void whenMapUpdateUserRequestToEntityAndIdIsNull_ThenReturnNull() {

            final User patch = mapper.mapToEntity(null, TestUpdateUserRequest.base());

            then(patch).isNull();
        }

        @DisplayName("""
                Given user's ID is not null and a request is null,
                when map ID and the request to a user,
                then return null
                """)
        @Test
        void whenMapUpdateUserRequestToEntityAndRequestIsNull_ThenReturnNull() {

            final User patch = mapper.mapToEntity(ID, null);

            then(patch).isNull();
        }

        @DisplayName("""
                Given user's ID is not null and a request is not null,
                when map ID and the request to a user,
                then return a correct user
                """)
        @Test
        void whenMapUpdateUserRequest_ThenReturnCorrectPatch() {

            final User patch = mapper.mapToEntity(ID, TestUpdateUserRequest.base());

            then(patch).usingRecursiveComparison().isEqualTo(TestUser.patch());
        }

        @DisplayName("""
                Given user's ID is not null and request's state is not null,
                when map ID and the request to a user.
                then user's state is correct
                """)
        @ParameterizedTest
        @EnumSource(UpdateUserRequest.State.class)
        void whenMapUpdateUserRequest_ThenAllStatesMappedCorrectly(final UpdateUserRequest.State state) {
            final UpdateUserRequest request = TestUpdateUserRequest.base(state);

            final User.State mappedState = mapper.mapToEntity(ID, request).getState();

            then(mappedState.name()).isEqualTo(state.name());
        }

        @DisplayName("""
                Given user's ID is not null and request's state is null,
                when map ID and the request to a user,
                then user's state is null
                """)
        @Test
        void whenMapUpdateUserRequest_ThenNullStateMappedCorrectly() {
            final UpdateUserRequest request = TestUpdateUserRequest.base(null);

            final User.State mappedState = mapper.mapToEntity(ID, request).getState();

            then(mappedState).isNull();
        }
    }

    @DisplayName("Map User to UserDto")
    @Nested
    class MapUserToDtoTest {

        @DisplayName("""
                Given a user is null,
                when map the user to DTO,
                then return null
                """)
        @Test
        void whenMapUserToDtoAndUserIsNull_ThenReturnNull() {

            final UserDto dto = mapper.mapToDto((User) null);

            then(dto).isNull();
        }

        @DisplayName("""
                Given a user is not null,
                when map the user to DTO,
                then return a correct DTO
                """)
        @Test
        void whenMapUserToDtoAndUserNotNull_ThenReturnCorrectDto() {

            final UserDto dto = mapper.mapToDto(TestUser.persisted());

            then(dto).isEqualTo(TestUserDto.base());
        }

        @DisplayName("""
                Given a user is not null and their state is null,
                when map the user to DTO,
                then DTO's state is null
                """)
        @Test
        void whenMapUserToDtoAndStateIsNull_ThenMappedStateIsAlsoNull() {
            final User user = TestUser.persisted();
            user.setState(null);

            final String mappedState = mapper.mapToDto(user).state();

            then(mappedState).isNull();
        }

        @DisplayName("""
                Given a list of users is null,
                when map the list to a list of DTOs,
                then return null
                """)
        @Test
        void whenMapListOfUsersToDtosAndListIsNull_ThenReturnNull() {

            final List<UserDto> dtos = mapper.mapToDto((List<User>) null);

            then(dtos).isNull();
        }

        @DisplayName("""
                Given a list of users is not empty,
                when map the list to a list of DTOs,
                then return a correct list of DTOs
                """)
        @Test
        void whenMapListOfUsersToDtosAndListNotNull_ThenReturnCorrectListOfDtos() {

            final List<UserDto> dtos = mapper.mapToDto(List.of(TestUser.persisted()));

            then(dtos).containsExactly(TestUserDto.base());
        }

        @DisplayName("""
                Given a list of users is empty,
                when map the list to a list of DTOs,
                then return an empty list
                """)
        @Test
        void whenMapListOfUsersToDtosAndListIsEmpty_ThenReturnEmptyList() {

            final List<UserDto> dtos = mapper.mapToDto(List.of());

            then(dtos).isEmpty();
        }
    }

    private static class MockUUIDGenerator implements AutoCloseable {

        private final MockedStatic<Generators> mock;

        public MockUUIDGenerator() {
            this.mock = Mockito.mockStatic(Generators.class);
            initMock();
        }

        private void initMock() {
            final TimeBasedEpochGenerator mockGenerator = Mockito.mock(TimeBasedEpochGenerator.class);
            when(Generators.timeBasedEpochGenerator()).thenReturn(mockGenerator);
            when(mockGenerator.generate()).thenReturn(ID);
        }

        @Override
        public void close() {
            mock.close();
        }
    }
}
