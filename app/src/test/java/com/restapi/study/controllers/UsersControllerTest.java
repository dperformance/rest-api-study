package com.restapi.study.controllers;

import com.restapi.study.application.UserService;
import com.restapi.study.domain.User;
import com.restapi.study.dto.UserRegisterData;
import com.restapi.study.dto.UserResultData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        given(userService.registerUser(any(UserRegisterData.class)))
                .will(invocation -> {
                    UserRegisterData userRegisterData =
                            invocation.getArgument(0);
                    return User.builder()
                            .email(userRegisterData.getEmail())
                            .password(userRegisterData.getPassword())
                            .name(userRegisterData.getName())
                            .build();

                });
    }

    @Test
    void registerUserWithValidAttribute() throws Exception {

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"dyson@naver.com\"," +
                                "\"name\":\"dyson\",\"password\":\"qwer1234\"}")
        )
                .andExpect(status().isCreated())
                .andExpect(content().string(
                        containsString("\"email\":\"dyson@naver.com\""))
                );

        verify(userService).registerUser(any(UserRegisterData.class));

    }


}