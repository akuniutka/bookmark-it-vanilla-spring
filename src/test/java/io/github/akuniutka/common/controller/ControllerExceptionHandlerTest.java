package io.github.akuniutka.common.controller;

import io.github.akuniutka.exception.DtoNotValidException;
import io.github.akuniutka.exception.DuplicateEmailException;
import io.github.akuniutka.exception.UserDeletedException;
import io.github.akuniutka.exception.UserNotFoundException;
import io.github.akuniutka.log.InjectLogCaptor;
import io.github.akuniutka.log.LogCaptor;
import io.github.akuniutka.log.LogEvents;
import io.github.akuniutka.log.WithLogCapture;
import io.github.akuniutka.user.entity.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
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
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

@DisplayName("ControllerExceptionHandler Unit Tests")
@WithLogCapture(ControllerExceptionHandler.class)
class ControllerExceptionHandlerTest {

    private final ControllerExceptionHandler exceptionHandler = new ControllerExceptionHandler();

    @InjectLogCaptor
    LogCaptor logCaptor;

    @DisplayName("""
            When handle HttpMessageNotReadableException,
            then log error message and return BAD_REQUEST
            """)
    @Test
    void whenHandleHttpMessageNotReadableException_ThenReturnProblemDetailAndLog() {
        final HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Cannot map value",
                emptyHttpMessage());

        final ProblemDetail response = exceptionHandler.handleHttpMessageNotReadableException(exception);

        then(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST.value())
                .hasFieldOrPropertyWithValue("detail", "Check data you sent is correct");
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "WARN", "Cannot map value"
        ));
    }

    @DisplayName("""
            Given Errors are null,
            when handle DtoNotValidException,
            then log error message and return BAD_REQUEST
            """)
    @Test
    void givenErrorsAreNull_WhenHandleDtoNotValidExceptionAndErrorsIsNull_ThenReturnProblemDetailWithoutErrorsAndLog() {
        final DtoNotValidException exception = new DtoNotValidException(null);

        final ProblemDetail response = exceptionHandler.handleDtoNotValidException(exception);

        then(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST.value())
                .hasFieldOrPropertyWithValue("detail", "Check data you sent is correct");
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "WARN", "DTO validation error: null"
        ));
    }

    @DisplayName("""
            Given Errors are not null,
            when handle DtoNotValidException,
            then log validation errors, return BAD_REQUEST and validation errors
            """)
    @Test
    void givenErrorsAreNotNull_WhenHandleDtoNotValidExceptionAndErrorsNotNull_ThenReturnProblemsDetailWithErrorsAndLog() {
        final DtoNotValidException exception = new DtoNotValidException(mockErrors());

        final ProblemDetail response = exceptionHandler.handleDtoNotValidException(exception);

        then(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST.value())
                .hasFieldOrPropertyWithValue("detail", "Check data you sent is correct")
                .hasFieldOrPropertyWithValue("properties",
                        Map.of("errors",
                                Map.of(
                                        "email", Set.of("", "exceeds"),
                                        "name", Set.of("contains", "exceeds")
                                )));
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "WARN", "DTO validation error: {email=[, exceeds], name=[contains, exceeds]}"
        ));
    }

    @DisplayName("""
            When handle UserNotFoundException,
            then log error message and return NOT_FOUND
            """)
    @Test
    void whenHandleUserNotFoundException_ThenReturnProblemDetailsAndLog() {
        final UserNotFoundException exception = new UserNotFoundException(ID);

        final ProblemDetail response = exceptionHandler.handleUserNotFoundException(exception);

        then(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND.value())
                .hasFieldOrPropertyWithValue("detail", "User %s does not exist".formatted(ID));
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "WARN", "User 92f08b0a-4302-40ff-823d-b9ce18522552 does not exist"
        ));
    }

    @DisplayName("""
            When handle DuplicateEmailException,
            then log error message and return CONFLICT
            """)
    @Test
    void whenHandleDuplicateEmailException_ThenReturnProblemDetailAndLog() {
        final DuplicateEmailException exception = new DuplicateEmailException(EMAIL);

        final ProblemDetail response = exceptionHandler.handleDuplicateEmailException(exception);

        then(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT.value())
                .hasFieldOrPropertyWithValue("detail", "User with email %s already registered".formatted(EMAIL));
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "WARN", "User with email john@mail.com already registered"
        ));
    }

    @DisplayName("""
            When handle UserDeletedException,
            then log error message and return CONFLICT
            """)
    @Test
    void whenHandleUserDeletedException_ThenReturnProblemDetailAndLog() {
        final UserDeletedException exception = new UserDeletedException(ID);

        final ProblemDetail response = exceptionHandler.handleUserDeletedException(exception);

        then(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT.value())
                .hasFieldOrPropertyWithValue("detail", "Cannot update user %s: user deleted".formatted(ID));
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "WARN", "Cannot update user 92f08b0a-4302-40ff-823d-b9ce18522552: user deleted"
        ));
    }

    @DisplayName("""
            When handle ObjectOptimisticLockingFailureException,
            then log error message and return CONFLICT
            """)
    @Test
    void whenHandleObjectOptimisticLockingFailureException_ThenReturnProblemRetailAndLog() {
        final ObjectOptimisticLockingFailureException exception = new ObjectOptimisticLockingFailureException(
                User.class, ID);

        final ProblemDetail response = exceptionHandler.handleObjectOptimisticLockingFailureException(exception);

        then(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT.value())
                .hasFieldOrPropertyWithValue("detail", "Someone updated data in parallel. Try again later.");
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "WARN",
                "Object of class [io.github.akuniutka.user.entity.User] with identifier "
                        + "[92f08b0a-4302-40ff-823d-b9ce18522552]: optimistic locking failed"
        ));
    }

    @DisplayName("""
            When handle any other Throwable,
            then log error message and exception, return INTERNAL_SERVER_ERROR
            """)
    @Test
    void whenHandleThrowable_ThenReturnProblemDetailAndLog() {
        final RuntimeException exception = new RuntimeException("Test exception");

        final ProblemDetail response = exceptionHandler.handleThrowable(exception);

        then(response)
                .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                .hasFieldOrPropertyWithValue("detail", "Please contact site admin");
        then(logCaptor.getEvents()).containsSubsequence(LogEvents.of(
                "ERROR", "Test exception"
        ));
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

    private Errors mockErrors() {
        final FieldError malformedError = Mockito.mock(FieldError.class);
        given(malformedError.getField()).willReturn("email");
        given(malformedError.getDefaultMessage()).willReturn(null);

        final Errors mockErrors = Mockito.mock(Errors.class);
        given(mockErrors.getFieldErrors()).willReturn(List.of(
                new FieldError("name", "name", "exceeds"),
                new FieldError("name", "name", "contains"),
                malformedError,
                new FieldError("email", "email", "exceeds")
        ));
        return mockErrors;
    }
}
