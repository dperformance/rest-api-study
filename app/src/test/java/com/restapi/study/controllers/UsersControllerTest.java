package com.restapi.study.controllers;

import com.restapi.study.application.AuthenticationService;
import com.restapi.study.application.UserService;
import com.restapi.study.domain.Role;
import com.restapi.study.domain.User;
import com.restapi.study.dto.UserModificationData;
import com.restapi.study.dto.UserRegisterData;
import com.restapi.study.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final Long EXISTED_ID = 1L;

    private static final Long NOT_EXISTED_ID = 1000L;

    private static final String MY_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    private static final String OTHER_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjJ9.TEM6MULsZeqkBbUKziCR4Dg_8kymmZkyxsCXlfNJ3g0";
    private static final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjEwMDR9.3GV5ZH3flBf0cnaXQCNNZlT4mgyFyBUhn3LKzQohh1A";


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() throws java.nio.file.AccessDeniedException {
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

        given(
                userService.updateUser(
                        eq(EXISTED_ID),
                        any(UserModificationData.class),
                        eq(EXISTED_ID)
                )
        )
                .will(invocation -> {
                    Long id = invocation.getArgument(0);
                   UserModificationData source = invocation.getArgument(1);
                   return User.builder()
                           .id(id)
                           .name(source.getName())
                           .build();
                });

        given(
                userService.updateUser(
                        eq(NOT_EXISTED_ID),
                        any(UserModificationData.class),
                        eq(EXISTED_ID)
                )
        )
                .willThrow(new UserNotFoundException(NOT_EXISTED_ID));

        given(
                userService.updateUser(
                        eq(1L),
                        any(UserModificationData.class),
                        eq(2L)
                )
        )
                .willThrow(new AccessDeniedException("Access denied"));

        given(userService.deleteUser(NOT_EXISTED_ID))
                .willThrow(new UserNotFoundException(NOT_EXISTED_ID));

        given(authenticationService.parseToken(MY_TOKEN)).willReturn(1L);
        given(authenticationService.parseToken(OTHER_TOKEN)).willReturn(2L);
        given(authenticationService.parseToken(ADMIN_TOKEN)).willReturn(1004L);

        given(authenticationService.roles(1L))
                .willReturn(Arrays.asList(new Role("USER")));
        given(authenticationService.roles(2L))
                        .willReturn(Arrays.asList(new Role("USER")));
        given(authenticationService.roles(1004L))
                                .willReturn(Arrays.asList(new Role("USER"),
                                        new Role("ADMIN")));
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

    @Test
    void updateUserWithValidAttribute() throws Exception {
        mockMvc.perform(
                patch("/users/{id}", EXISTED_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UPDATE\",\"password\":\"update1234\"}")
                        .header("Authorization", "Bearer " + MY_TOKEN)

        )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"name\":\"UPDATE\""))
                );

        verify(userService).updateUser(eq(EXISTED_ID), any(UserModificationData.class), eq(EXISTED_ID));
    }

    @Test
    void updateUserInvalidAttribute() throws Exception {
        mockMvc.perform(
          patch("/users/{id}", EXISTED_ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\":\"\",\"password\":\"\"}")
                  .header("Authorization", "Bearer " + MY_TOKEN)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithNotExistedId() throws Exception {
        mockMvc.perform(
          patch("/users/{id}", NOT_EXISTED_ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
                  .header("Authorization", "Bearer " + MY_TOKEN)
        )
                .andExpect(status().isNotFound());

        verify(userService).updateUser(
                eq(NOT_EXISTED_ID),
                any(UserModificationData.class),
                eq(EXISTED_ID));
    }

    @Test
    void updateUserWithoutAccessToken() throws Exception {
        mockMvc.perform(
          patch("/users/{id}", EXISTED_ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateUserWithOthersAccessToken() throws Exception {
        mockMvc.perform(
          patch("/users/{id}", EXISTED_ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
                  .header("Authorization", "Bearer " + OTHER_TOKEN)
        )
                .andExpect(status().isForbidden());

        verify(userService)
                .updateUser(eq(EXISTED_ID), any(UserModificationData.class), eq(2L));
    }

    @Test
    void destroyWithExistedId() throws Exception {
        mockMvc.perform(
                delete("/users/{id}", EXISTED_ID)
                        .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isOk());

        verify(userService).deleteUser(EXISTED_ID);
    }

    @Test
    void destroyWithNotExistedId() throws Exception {
        mockMvc.perform(
                delete("/users/{id}", NOT_EXISTED_ID)
                        .header("Authorization", "Bearer " + ADMIN_TOKEN))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(NOT_EXISTED_ID);
    }

    @Test
    void destroyWithoutAccessToken() throws Exception {
        mockMvc.perform(
                delete("/users/{id}", EXISTED_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void destroyWithoutAdminRole() throws Exception {
        mockMvc.perform(
                delete("/users/{id}", EXISTED_ID)
                        .header("Authorization", "Bearer " + MY_TOKEN))
                .andExpect(status().isForbidden());
    }

}