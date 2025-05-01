package io.github.akuniutka.common.controller;

import io.github.akuniutka.exception.DtoNotValidException;
import io.github.akuniutka.exception.DuplicateEmailException;
import io.github.akuniutka.exception.UserDeletedException;
import io.github.akuniutka.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler
    public ProblemDetail handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        log.warn(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Check data you sent is correct");
    }

    @ExceptionHandler
    public ProblemDetail handleDtoNotValidException(final DtoNotValidException exception) {
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Check data you sent is correct");
        Map<String, Set<String>> errors = null;
        if (exception.getErrors() != null) {
            errors = exception.getErrors().getFieldErrors().stream()
                    .collect(
                            groupingBy(
                                    FieldError::getField,
                                    TreeMap::new,
                                    mapping(
                                            error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""),
                                            toCollection(TreeSet::new))));
            response.setProperty("errors", errors);
        }
        log.warn("DTO validation error: {}", errors);
        return response;
    }

    @ExceptionHandler
    public ProblemDetail handleUserNotFoundException(final UserNotFoundException exception) {
        log.warn(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler
    public ProblemDetail handleDuplicateEmailException(final DuplicateEmailException exception) {
        log.warn(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler
    public ProblemDetail handleUserDeletedException(final UserDeletedException exception) {
        log.warn(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler
    public ProblemDetail handleObjectOptimisticLockingFailureException(
            final ObjectOptimisticLockingFailureException exception) {
        log.warn(exception.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Someone updated data in parallel. Try again later.");
    }

    @ExceptionHandler
    public ProblemDetail handleThrowable(final Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Please contact site admin");
    }
}
