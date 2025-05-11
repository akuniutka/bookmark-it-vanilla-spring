package io.github.akuniutka.user.service;

import io.github.akuniutka.config.ApplicationTestConfig;
import io.github.akuniutka.exception.DuplicateEmailException;
import io.github.akuniutka.exception.UserDeletedException;
import io.github.akuniutka.exception.UserNotFoundException;
import io.github.akuniutka.user.TestUser;
import io.github.akuniutka.user.entity.User;
import io.github.akuniutka.user.repository.UserRepository;
import io.github.akuniutka.util.LogListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static io.github.akuniutka.user.TestUser.EMAIL;
import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.user.TestUser.OTHER_EMAIL;
import static io.github.akuniutka.user.TestUser.UPPERCASE_EMAIL;
import static io.github.akuniutka.util.TestUtils.assertLogs;
import static io.github.akuniutka.util.TestUtils.deepEqual;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@DisplayName("UserServiceImpl Unit Tests")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final LogListener logListener = new LogListener(UserServiceImpl.class);

    @Mock
    private UserRepository mockRepository;

    private UserService service;

    @BeforeEach
    void setUp() {
        logListener.startListen();
        logListener.reset();
        service = new UserServiceImpl(mockRepository, ApplicationTestConfig.fixedClock());
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
    }

    @DisplayName("Add a new user")
    @Nested
    class AddUserTest {

        @DisplayName("""
                When add a null user,
                then throw an exception
                """)
        @Test
        void whenAddUserAndUserIsNull_ThenThrowIllegalArgumentException() {

            final Throwable throwable = catchThrowable(() -> service.addUser(null));

            then(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("user is marked non-null but is null");
        }

        @DisplayName("""
                Given another user exists,
                when add a new user with the same email,
                then throw an exception
                """)
        @Test
        void whenAddUserAndEmailAlreadyExist_ThenThrowDuplicateEmailException() {
            final User user = TestUser.fresh();
            given(mockRepository.existsByEmailIgnoreCase(EMAIL)).willReturn(true);

            final Throwable throwable = catchThrowable(() -> service.addUser(user));

            then(throwable)
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasFieldOrPropertyWithValue("email", EMAIL);
        }

        @DisplayName("""
                Given no other user with same email exists,
                when add a new user,
                then add user state and registration date, save and return the updated user, log success
                """)
        @Test
        void whenAddUser_ThenAddStateRegistrationDateAndSaveToRepositoryAndReturnUserAndLog() throws Exception {
            given(mockRepository.save(deepEqual(TestUser.base()))).willReturn(TestUser.persisted());

            final User user = service.addUser(TestUser.fresh());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
            assertLogs(logListener.getEvents(), "add_user.json", getClass());
        }
    }

    @DisplayName("Get a list of all users")
    @Nested
    class FindAllUsersTest {

        @DisplayName("""
                Given some users exist,
                when find all users,
                then return a list of all users
                """)
        @Test
        void whenFindAllUsers_ThenReturnUserList() {
            given(mockRepository.findAll()).willReturn(List.of(TestUser.persisted()));

            final List<User> users = service.findAllUsers();

            then(users).usingRecursiveComparison().isEqualTo(List.of(TestUser.persisted()));
        }

        @DisplayName("""
                Given no user exists,
                when find all users,
                then return an empty list
                """)
        @Test
        void whenFindAllUsersAndNoUsers_ThenReturnEmptyList() {
            given(mockRepository.findAll()).willReturn(List.of());

            final List<User> users = service.findAllUsers();

            then(users).isEmpty();
        }
    }

    @DisplayName("Get a user by their ID")
    @Nested
    class GetUserByIdTest {

        @DisplayName("""
                When get a user by null ID,
                then throw an exception
                """)
        @Test
        void whenGetUserByIdAndIdIsNull_ThenThrowIllegalArgumentException() {

            final Throwable throwable = catchThrowable(() -> service.getUserById(null));

            then(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("id is marked non-null but is null");
        }

        @DisplayName("""
                Given no user exists with ID specified,
                when get a user by that ID,
                then throw an exception
                """)
        @Test
        void whenGetUserByIdAndUserNotExist_ThenThrowUserNotFoundException() {
            given(mockRepository.findById(ID)).willReturn(Optional.empty());

            final Throwable throwable = catchThrowable(() -> service.getUserById(ID));

            then(throwable)
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("userId", ID);
        }

        @DisplayName("""
                Given a user exists,
                when get the user by their ID,
                then return the user
                """)
        @Test
        void whenGetUserByIdeAndUserExist_ThenReturnUser() {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));

            final User user = service.getUserById(ID);

            then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
        }
    }

    @DisplayName("Update a user")
    @Nested
    class UpdateUserTest {

        @DisplayName("""
                When apply a null patch,
                then throw an exception
                """)
        @Test
        void whenUpdateUserAndPatchIsNull_ThenThrowIllegalArgumentException() {

            final Throwable throwable = catchThrowable(() -> service.updateUser(null));

            then(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("patch is marked non-null but is null");
        }

        @DisplayName("""
                Given no user exists with ID specified,
                when apply a patch with that ID,
                then throw an exception
                """)
        @Test
        void whenUpdateUserAndUserNotExist_ThenThrowUserNotFound() {
            final User patch = TestUser.patch();
            given(mockRepository.findById(ID)).willReturn(Optional.empty());

            final Throwable throwable = catchThrowable(() -> service.updateUser(patch));

            then(throwable)
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("userId", ID);
        }

        @DisplayName("""
                Given a user is deleted,
                when patch that user,
                than throw an exception
                """)
        @Test
        void whenUpdateUserAndUserDeleted_ThenThrowUserDeletedException() {
            final User patch = TestUser.patch();
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.deleted()));

            final Throwable throwable = catchThrowable(() -> service.updateUser(patch));

            then(throwable)
                    .isInstanceOf(UserDeletedException.class)
                    .hasFieldOrPropertyWithValue("userId", ID);
        }

        @DisplayName("""
                Given a user exists and is not deleted and another user exists with an email in the patch,
                when patch all first user's properties,
                then throw an exception
                """)
        @Test
        void whenUpdateUserAndNewEmailAlreadyExist_ThenThrowDuplicateEmailException() {
            final User patch = TestUser.patch();
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.existsByEmailIgnoreCase(OTHER_EMAIL)).willReturn(true);

            final Throwable throwable = catchThrowable(() -> service.updateUser(patch));

            then(throwable)
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasFieldOrPropertyWithValue("email", OTHER_EMAIL);
        }

        @DisplayName("""
                Given a user exists and is not deleted and another user exists with an email in the patch,
                when patch first user's email only,
                then throw an exception
                """)
        @Test
        void whenUpdateUserWithNewEmailOnlyAndThatEmailAlreadyExist_ThenThrowDuplicateEmailException() {
            final User patch = TestUser.patchWithNewEmailOnly();
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.existsByEmailIgnoreCase(OTHER_EMAIL)).willReturn(true);

            final Throwable throwable = catchThrowable(() -> service.updateUser(patch));

            then(throwable)
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasFieldOrPropertyWithValue("email", OTHER_EMAIL);
        }

        @DisplayName("""
                Given a user exists and is not deleted and no user exists with an email in the patch,
                when patch all user's properties,
                then update user's properties, save and return the updated user, log success
                """)
        @Test
        void whenUpdateUser_ThenPatchUserAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.patched()))).willReturn(TestUser.patched());

            final User user = service.updateUser(TestUser.patch());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.patched());
            assertLogs(logListener.getEvents(), "patch_all_user_fields.json", getClass());
        }

        @DisplayName("""
                Given a user exists and is not deleted,
                when patch user's email to the same email in different case among other properties,
                then update user's properties, save and return the updated user, log success
                """)
        @Test
        void whenUpdateUserAndUppercaseEmail_ThenPatchUserAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.patchedWithUppercaseEmail())))
                    .willReturn(TestUser.patchedWithUppercaseEmail());
            lenient().when(mockRepository.existsByEmailIgnoreCase(UPPERCASE_EMAIL)).thenReturn(true);

            final User user = service.updateUser(TestUser.patchWithUppercaseEmail());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithUppercaseEmail());
            assertLogs(logListener.getEvents(), "patch_user_with_uppercase_email.json", getClass());
        }

        @DisplayName("""
                Given a user exists and is not deleted,
                when patch user's first name only,
                then update user's first name, save and return the updated user, log success
                """)
        @Test
        void whenUpdateUserAndFirstNameOnly_ThenUpdateFirstNameAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.patchedWithFirstNameOnly()))).willReturn(
                    TestUser.patchedWithFirstNameOnly());

            final User user = service.updateUser(TestUser.patchWithFirstNameOnly());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithFirstNameOnly());
            assertLogs(logListener.getEvents(), "patch_first_name_only.json", getClass());
        }

        @DisplayName("""
                Given a user exists and is not deleted,
                when patch user's last name only,
                then update user's last name, save and return the updated user, log success
                """)
        @Test
        void whenUpdateUserAndLastNameOnly_ThenUpdateLastNameAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.patchedWithLastNameOnly())))
                    .willReturn(TestUser.patchedWithLastNameOnly());

            final User user = service.updateUser(TestUser.patchWithLastNameOnly());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithLastNameOnly());
            assertLogs(logListener.getEvents(), "patch_last_name_only.json", getClass());
        }

        @DisplayName("""
                Given a user exists and is not deleted and no user exists with an email in the patch,
                when patch user's email only,
                then update user's email, save and return the updated user, log success
                """)
        @Test
        void whenUpdateUserAndNewEmailOnly_ThenUpdateEmailAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.patchedWithNewEmailOnly())))
                    .willReturn(TestUser.patchedWithNewEmailOnly());

            final User user = service.updateUser(TestUser.patchWithNewEmailOnly());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithNewEmailOnly());
            assertLogs(logListener.getEvents(), "patch_new_email_only.json", getClass());
        }

        @DisplayName("""
                Given a user exists and is not deleted,
                when patch user's email to the same email in different case only,
                then update user's email, save and return the updated user, log success
                """)
        @Test
        void whenUpdateUserAndUppercaseEmailOnly_ThenUpdateEmailAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.patchedWithUppercaseEmailOnly())))
                    .willReturn(TestUser.patchedWithUppercaseEmailOnly());
            lenient().when(mockRepository.existsByEmailIgnoreCase(UPPERCASE_EMAIL)).thenReturn(true);

            final User user = service.updateUser(TestUser.patchWithUppercaseEmailOnly());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithUppercaseEmailOnly());
            assertLogs(logListener.getEvents(), "patch_uppercase_email_only.json", getClass());
        }

        @DisplayName("""
                Given a user exists and is not deleted,
                when patch user's state only,
                then update user's state, save and return the updated user, log success
                """)
        @Test
        void whenUpdateUserAndStateOnly_ThenUpdateStateAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.patchedWithStateOnly())))
                    .willReturn(TestUser.patchedWithStateOnly());

            final User user = service.updateUser(TestUser.patchWithStateOnly());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithStateOnly());
            assertLogs(logListener.getEvents(), "patch_state_only.json", getClass());
        }

        @DisplayName("""
                Given a user exists and is not deleted,
                when patch no user's properties,
                then save and return the user with no changes, log success
                """)
        @Test
        void whenUpdateUserAndEmptyFields_ThenUpdateNothingAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.persisted()))).willReturn(TestUser.persisted());

            final User user = service.updateUser(TestUser.patchWithEmptyFields());

            then(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
            assertLogs(logListener.getEvents(), "patch_with_empty_fields.json", getClass());
        }
    }

    @DisplayName("Delete a user by their ID")
    @Nested
    class DeleteUserByIdTest {

        @DisplayName("""
                When delete a user by null ID,
                then throw an exception
                """)
        @Test
        void whenDeleteUserByIdAndIdIsNull_ThenThrowIllegalArgumentException() {

            final Throwable throwable = catchThrowable(() -> service.deleteUserById(null));

            then(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("id is marked non-null but is null");
        }

        @DisplayName("""
                Given no user exists with ID specified,
                when delete a user by that ID,
                then throw an exception
                """)
        @Test
        void whenDeleteUserByIdAndUserNotExist_ThenThrowUserNotFoundException() {
            given(mockRepository.findById(ID)).willReturn(Optional.empty());

            final Throwable throwable = catchThrowable(() -> service.deleteUserById(ID));

            then(throwable)
                    .isInstanceOf(UserNotFoundException.class)
                    .hasFieldOrPropertyWithValue("userId", ID);
        }

        @DisplayName("""
                Given a user exists,
                when delete the user by their ID,
                then change user state to DELETED, save and return the updated user, log success
                """)
        @Test
        void whenDeleteUserByIdAndUserExist_ThenMarkUserDeletedAndSaveToRepositoryAndReturnAndLog() throws Exception {
            given(mockRepository.findById(ID)).willReturn(Optional.of(TestUser.persisted()));
            given(mockRepository.save(deepEqual(TestUser.deleted()))).willReturn(TestUser.deleted());

            final User user = service.deleteUserById(ID);

            then(user).usingRecursiveComparison().isEqualTo(TestUser.deleted());
            assertLogs(logListener.getEvents(), "delete_user_by_id.json", getClass());
        }
    }
}
