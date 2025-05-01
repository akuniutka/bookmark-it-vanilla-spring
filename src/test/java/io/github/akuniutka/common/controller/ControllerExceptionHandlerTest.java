package io.github.akuniutka.common.controller;

import io.github.akuniutka.exception.DtoNotValidException;
import io.github.akuniutka.exception.DuplicateEmailException;
import io.github.akuniutka.exception.UserDeletedException;
import io.github.akuniutka.exception.UserNotFoundException;
import io.github.akuniutka.user.entity.User;
import io.github.akuniutka.util.LogListener;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.akuniutka.user.TestUser.EMAIL;
import static io.github.akuniutka.user.TestUser.ID;
import static io.github.akuniutka.util.TestUtils.assertLogs;
import static org.assertj.core.api.Assertions.assertThat;

class ControllerExceptionHandlerTest {

    private static final LogListener logListener = new LogListener(ControllerExceptionHandler.class);

    private ControllerExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        logListener.startListen();
        logListener.reset();
        exceptionHandler = new ControllerExceptionHandler();
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
    }

    @Test
    void whenHandleHttpMessageNotReadableException_ThenReturnProblemDetailAndLog() throws Exception {
        final HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Cannot map value",
                emptyHttpMessage());

        final ProblemDetail response = exceptionHandler.handleHttpMessageNotReadableException(exception);

        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST.value())
                .hasFieldOrPropertyWithValue("detail", "Check data you sent is correct");
        assertLogs(logListener.getEvents(), "http_message_not_readable_exception.json", getClass());
    }

    @Test
    void whenHandleDtoNotValidExceptionAndErrorsIsNull_ThenReturnProblemDetailWithoutErrorsAndLog() throws Exception {
        final DtoNotValidException exception = new DtoNotValidException(null);

        final ProblemDetail response = exceptionHandler.handleDtoNotValidException(exception);

        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST.value())
                .hasFieldOrPropertyWithValue("detail", "Check data you sent is correct");
        assertLogs(logListener.getEvents(), "dto_not_valid_exception_with_null.json", getClass());
    }

    @Test
    void whenHandleDtoNotValidExceptionAndErrorsNotNull_ThenReturnProblemsDetailWithErrorsAndLog() throws Exception {
        final FieldError malformedError = Mockito.mock(FieldError.class);
        Mockito.when(malformedError.getField()).thenReturn("email");
        Mockito.when(malformedError.getDefaultMessage()).thenReturn(null);
        final Errors mockErrors = Mockito.mock(Errors.class);
        Mockito.when(mockErrors.getFieldErrors()).thenReturn(List.of(
                new FieldError("name", "name", "exceeds"),
                new FieldError("name", "name", "contains"),
                malformedError,
                new FieldError("email", "email", "exceeds")));
        final DtoNotValidException exception = new DtoNotValidException(mockErrors);

        final ProblemDetail response = exceptionHandler.handleDtoNotValidException(exception);

        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST.value())
                .hasFieldOrPropertyWithValue("detail", "Check data you sent is correct")
                .hasFieldOrPropertyWithValue("properties",
                        Map.of("errors",
                                Map.of(
                                        "email", Set.of("", "exceeds"),
                                        "name", Set.of("contains", "exceeds")
                                )));
        assertLogs(logListener.getEvents(), "dto_not_valid_exception.json", getClass());
    }

    @Test
    void whenHandleUserNotFoundException_ThenReturnProblemDetailsAndLog() throws Exception {
        final UserNotFoundException exception = new UserNotFoundException(ID);

        final ProblemDetail response = exceptionHandler.handleUserNotFoundException(exception);

        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND.value())
                .hasFieldOrPropertyWithValue("detail", "User %s does not exist".formatted(ID));
        assertLogs(logListener.getEvents(), "user_not_found_exception.json", getClass());
    }

    @Test
    void whenHandleDuplicateEmailException_ThenReturnProblemDetailAndLog() throws Exception {
        final DuplicateEmailException exception = new DuplicateEmailException(EMAIL);

        final ProblemDetail response = exceptionHandler.handleDuplicateEmailException(exception);

        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT.value())
                .hasFieldOrPropertyWithValue("detail", "User with email %s already registered".formatted(EMAIL));
        assertLogs(logListener.getEvents(), "duplicate_email_exception.json", getClass());
    }

    @Test
    void whenHandleUserDeletedException_ThenReturnProblemDetailAndLog() throws Exception {
        final UserDeletedException exception = new UserDeletedException(ID);

        final ProblemDetail response = exceptionHandler.handleUserDeletedException(exception);

        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT.value())
                .hasFieldOrPropertyWithValue("detail", "Cannot update user %s: user deleted".formatted(ID));
        assertLogs(logListener.getEvents(), "user_deleted_exception.json", getClass());
    }

    @Test
    void whenHandleObjectOptimisticLockingFailureException_ThenReturnProblemRetailAndLog() throws  Exception {
        final ObjectOptimisticLockingFailureException exception = new ObjectOptimisticLockingFailureException(
                User.class, ID);

        final ProblemDetail response = exceptionHandler.handleObjectOptimisticLockingFailureException(exception);

        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT.value())
                .hasFieldOrPropertyWithValue("detail", "Someone updated data in parallel. Try again later.");
        assertLogs(logListener.getEvents(), "object_optimistic_locking_failure-exception.json", getClass());
    }

    @Test
    void whenHandleThrowable_ThenReturnProblemDetailAndLog() throws Exception {
        final RuntimeException exception = new RuntimeException("Test exception");

        final ProblemDetail response = exceptionHandler.handleThrowable(exception);

        assertThat(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                .hasFieldOrPropertyWithValue("detail", "Please contact site admin");
        assertLogs(logListener.getEvents(), "throwable.json", getClass());
    }

    private HttpInputMessage emptyHttpMessage() {
        return new HttpInputMessage() {
            @Override
            public @NotNull InputStream getBody() {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public @NotNull HttpHeaders getHeaders() {
                return new HttpHeaders();
            }
        };
    }
}
