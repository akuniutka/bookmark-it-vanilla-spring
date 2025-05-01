package io.github.akuniutka.common.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static io.github.akuniutka.util.TestUtils.loadJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class ControllerExceptionHandlerIT {

    private static final String URL = "/not-existing-endpoint";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ControllerExceptionHandler()).build();
    }

    @Test
    void whenExceptionInWebLayer_ThenInvokeControllerExceptionHandler() throws Exception {
        final String responseBody = loadJson("internal_server_error.json", getClass());

        mockMvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isInternalServerError(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(responseBody, true));
    }
}
