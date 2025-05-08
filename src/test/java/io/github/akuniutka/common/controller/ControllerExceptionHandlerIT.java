package io.github.akuniutka.common.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static io.github.akuniutka.util.TestUtils.loadJson;
import static org.assertj.core.api.BDDAssertions.then;

class ControllerExceptionHandlerIT {

    private static final String URL = "/not-existing-endpoint";

    private final MockMvcTester mockMvcTester = MockMvcTester.of(new ControllerExceptionHandler());

    @Test
    void whenExceptionInWebLayer_ThenInvokeControllerExceptionHandler() throws Exception {
        final String responseBody = loadJson("internal_server_error.json", getClass());

        final MvcTestResult response = mockMvcTester
                .get()
                .uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().isEqualTo(responseBody);
    }
}
