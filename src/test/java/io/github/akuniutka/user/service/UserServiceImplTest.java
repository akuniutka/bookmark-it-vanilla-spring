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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static io.github.akuniutka.user.TestUser.EMAIL;
import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.user.TestUser.OTHER_EMAIL;
import static io.github.akuniutka.util.TestUtils.assertLogs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final LogListener logListener = new LogListener(UserServiceImpl.class);

    @Mock
    private UserRepository mockRepository;

    private UserService service;
    private InOrder inOrder;

    @BeforeEach
    void setUp() {
        inOrder = Mockito.inOrder(mockRepository);
        logListener.startListen();
        logListener.reset();
        service = new UserServiceImpl(mockRepository, ApplicationTestConfig.fixedClock());
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository);
    }

    @Test
    void whenAddUserAndUserIsNull_ThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> service.addUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user is marked non-null but is null");
    }

    @Test
    void whenAddUserAndEmailAlreadyExist_ThenThrowDuplicateEmailException() {
        final User user = TestUser.fresh();
        when(mockRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> service.addUser(user))
                .isInstanceOf(DuplicateEmailException.class)
                .hasFieldOrPropertyWithValue("email", EMAIL);
        verify(mockRepository).existsByEmailIgnoreCase(EMAIL);
    }

    @Test
    void whenAddUser_ThenAddStateRegistrationDateAndSaveToRepositoryAndReturnUserAndLog() throws Exception {
        when(mockRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(mockRepository.save(any())).thenReturn(TestUser.persisted());

        final User user = service.addUser(TestUser.fresh());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
        assertLogs(logListener.getEvents(), "add_user.json", getClass());
        inOrder.verify(mockRepository).existsByEmailIgnoreCase(EMAIL);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.base())));
    }

    @Test
    void whenFindAllUsers_ThenReturnUserList() {
        when(mockRepository.findAll()).thenReturn(List.of(TestUser.persisted()));

        final List<User> users = service.findAllUsers();

        assertThat(users).usingRecursiveComparison().isEqualTo(List.of(TestUser.persisted()));
        verify(mockRepository).findAll();
    }

    @Test
    void whenFindAllUsersAndNoUsers_ThenReturnEmptyList() {
        when(mockRepository.findAll()).thenReturn(List.of());

        final List<User> users = service.findAllUsers();

        assertThat(users).isEmpty();
        verify(mockRepository).findAll();
    }

    @Test
    void whenGetUserByIdAndIdIsNull_ThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> service.getUserById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id is marked non-null but is null");
    }

    @Test
    void whenGetUserByIdAndUserNotExist_ThenThrowUserNotFoundException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserById(ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasFieldOrPropertyWithValue("userId", ID);
        verify(mockRepository).findById(ID);
    }

    @Test
    void whenGetUserByIdeAndUserExist_ThenReturnUser() {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));

        final User user = service.getUserById(ID);

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
        verify(mockRepository).findById(ID);
    }

    @Test
    void whenUpdateUserAndPatchIsNull_ThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> service.updateUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("patch is marked non-null but is null");
    }

    @Test
    void whenUpdateUserAndUserNotExist_ThenThrowUserNotFound() {
        final User patch = TestUser.patch();
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUser(patch))
                .isInstanceOf(UserNotFoundException.class)
                .hasFieldOrPropertyWithValue("userId", ID);
        verify(mockRepository).findById(ID);
    }

    @Test
    void whenUpdateUserAndUserDeleted_ThenThrowUserDeletedException() {
        final User patch = TestUser.patch();
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.deleted()));

        assertThatThrownBy(() -> service.updateUser(patch))
                .isInstanceOf(UserDeletedException.class)
                .hasFieldOrPropertyWithValue("userId", ID);
        verify(mockRepository).findById(ID);
    }

    @Test
    void whenUpdateUserAndNewEmailAlreadyExist_ThenThrowDuplicateEmailException() {
        final User patch = TestUser.patch();
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.existsByEmailIgnoreCase(any())).thenReturn(true);

        assertThatThrownBy(() -> service.updateUser(patch))
                .isInstanceOf(DuplicateEmailException.class)
                .hasFieldOrPropertyWithValue("email", OTHER_EMAIL);
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).existsByEmailIgnoreCase(OTHER_EMAIL);
    }

    @Test
    void whenUpdateUserWithNewEmailOnlyAndThatEmailAlreadyExist_ThenThrowDuplicateEmailException() {
        final User patch = TestUser.patchWithNewEmailOnly();
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.existsByEmailIgnoreCase(any())).thenReturn(true);

        assertThatThrownBy(() -> service.updateUser(patch))
                .isInstanceOf(DuplicateEmailException.class)
                .hasFieldOrPropertyWithValue("email", OTHER_EMAIL);
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).existsByEmailIgnoreCase(OTHER_EMAIL);
    }

    @Test
    void whenUpdateUserAndNewEmailNotExist_ThenPatchUserAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.existsByEmailIgnoreCase(any())).thenReturn(false);
        when(mockRepository.save(any())).thenReturn(TestUser.patched());

        final User user = service.updateUser(TestUser.patch());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.patched());
        assertLogs(logListener.getEvents(), "patch_all_user_fields.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).existsByEmailIgnoreCase(OTHER_EMAIL);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.patched())));
    }

    @Test
    void whenUpdateUserAndUppercaseEmail_ThenPatchUserAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.save(any())).thenReturn(TestUser.patchedWithUppercaseEmail());

        final User user = service.updateUser(TestUser.patchWithUppercaseEmail());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithUppercaseEmail());
        assertLogs(logListener.getEvents(), "patch_user_with_uppercase_email.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.patchedWithUppercaseEmail())));
    }

    @Test
    void whenUpdateUserAndFirstNameOnly_ThenUpdateFirstNameAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.save(any())).thenReturn(TestUser.patchedWithFirstNameOnly());

        final User user = service.updateUser(TestUser.patchWithFirstNameOnly());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithFirstNameOnly());
        assertLogs(logListener.getEvents(), "patch_first_name_only.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.patchedWithFirstNameOnly())));
    }

    @Test
    void whenUpdateUserAndLastNameOnly_ThenUpdateLastNameAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.save(any())).thenReturn(TestUser.patchedWithLastNameOnly());

        final User user = service.updateUser(TestUser.patchWithLastNameOnly());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithLastNameOnly());
        assertLogs(logListener.getEvents(), "patch_last_name_only.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.patchedWithLastNameOnly())));
    }

    @Test
    void whenUpdateUserAndNewEmailOnly_ThenUpdateEmailAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.existsByEmailIgnoreCase(any())).thenReturn(false);
        when(mockRepository.save(any())).thenReturn(TestUser.patchedWithNewEmailOnly());

        final User user = service.updateUser(TestUser.patchWithNewEmailOnly());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithNewEmailOnly());
        assertLogs(logListener.getEvents(), "patch_new_email_only.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).existsByEmailIgnoreCase(OTHER_EMAIL);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.patchedWithNewEmailOnly())));
    }

    @Test
    void whenUpdateUserAndUppercaseEmailOnly_ThenUpdateEmailAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.save(any())).thenReturn(TestUser.patchedWithUppercaseEmailOnly());

        final User user = service.updateUser(TestUser.patchWithUppercaseEmailOnly());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithUppercaseEmailOnly());
        assertLogs(logListener.getEvents(), "patch_uppercase_email_only.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.patchedWithUppercaseEmailOnly())));
    }

    @Test
    void whenUpdateUserAndStateOnly_ThenUpdateStateAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.save(any())).thenReturn(TestUser.patchedWithStateOnly());

        final User user = service.updateUser(TestUser.patchWithStateOnly());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.patchedWithStateOnly());
        assertLogs(logListener.getEvents(), "patch_state_only.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.patchedWithStateOnly())));
    }

    @Test
    void whenUpdateUserAndEmptyFields_ThenUpdateNothingAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.save(any())).thenReturn(TestUser.persisted());

        final User user = service.updateUser(TestUser.patchWithEmptyFields());

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.persisted());
        assertLogs(logListener.getEvents(), "patch_with_empty_fields.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.persisted())));
    }

    @Test
    void whenDeleteUserByIdAndIdIsNull_ThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> service.deleteUserById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id is marked non-null but is null");
    }

    @Test
    void whenDeleteUserByIdAndUserNotExist_ThenThrowUserNotFoundException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteUserById(ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasFieldOrPropertyWithValue("userId", ID);
        verify(mockRepository).findById(ID);
    }

    @Test
    void whenDeleteUserByIdAndUserExist_ThenMarkUserDeletedAndSaveToRepositoryAndReturnAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestUser.persisted()));
        when(mockRepository.save(any())).thenReturn(TestUser.deleted());

        final User user = service.deleteUserById(ID);

        assertThat(user).usingRecursiveComparison().isEqualTo(TestUser.deleted());
        assertLogs(logListener.getEvents(), "delete_user_by_id.json", getClass());
        inOrder.verify(mockRepository).findById(ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestUser.deleted())));
    }
}
