package com.restapi.study.controllers;

import com.restapi.study.application.AuthenticationService;
import com.restapi.study.exception.LoginFailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    private static final String VALID_EMAIL = "valid@gmail.com";
    private static final String INVALID_EMAIL = "invalid@gmail.com";

    private static final String VALID_PASSWORD = "valid1234";
    private static final String INVALID_PASSWORD = "invalid1234";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        given(authenticationService.login(VALID_EMAIL, VALID_PASSWORD))
                .willReturn("a.b.c");

        given(authenticationService.login(INVALID_EMAIL, INVALID_PASSWORD))
                .willThrow(new LoginFailException(""));
    }

    @Test
    void loginWithRightEmailAndPassword() throws Exception {
        mockMvc.perform(
                post("/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"valid@gmail.com\"," +
                                "\"password\":\"valid1234\"}")
                )
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(".")));
    }

    @Test
    void loginWithWrongEmailAndPassword() throws Exception {
        mockMvc.perform(
                post("/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid@gmail.com\"," +
                                "\"password\":\"invalid1234\"}")
                )
                .andExpect(status().isBadRequest());
    }


}