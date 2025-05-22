package io.github.akuniutka.common.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("ControllerExceptionHandler Integration Tests")
class ControllerExceptionHandlerIT {

    private static final String URL = "/not-existing-endpoint";

    private final MockMvcTester mockMvcTester = MockMvcTester.of(new ControllerExceptionHandler());

    @DisplayName("""
            When GET at not existing endpoint,
            then respond with INTERNAL_SERVER_ERROR and application/problem+json and a custom message
            """)
    @Test
    void whenExceptionInWebLayer_ThenInvokeControllerExceptionHandler() {

        final MvcTestResult response = mockMvcTester
                .get()
                .uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().isEqualTo("""
                        {
                          "type": "about:blank",
                          "title": "Internal Server Error",
                          "status": 500,
                          "detail": "Please contact site admin",
                          "instance": "/not-existing-endpoint"
                        }
                        """);
    }
}
