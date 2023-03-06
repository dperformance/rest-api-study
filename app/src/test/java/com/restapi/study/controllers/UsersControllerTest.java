package com.restapi.study.controllers;

import com.restapi.study.application.UserService;
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
import org.springframework.test.web.servlet.MockMvc;

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

        given(userService.updateUser(eq(EXISTED_ID), any(UserModificationData.class)))
                .will(invocation -> {
                    Long id = invocation.getArgument(0);
                   UserModificationData source = invocation.getArgument(1);
                   return User.builder()
                           .id(id)
                           .name(source.getName())
                           .build();
                });

        given(userService.updateUser(eq(NOT_EXISTED_ID), any(UserModificationData.class)))
                .willThrow(new UserNotFoundException(NOT_EXISTED_ID));

        given(userService.deleteUser(NOT_EXISTED_ID))
                .willThrow(new UserNotFoundException(NOT_EXISTED_ID));
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

        )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"name\":\"UPDATE\""))
                );

        verify(userService).updateUser(eq(EXISTED_ID), any(UserModificationData.class));
    }

    @Test
    void updateUserInvalidAttribute() throws Exception {
        mockMvc.perform(
          patch("/users/{id}", EXISTED_ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\":\"\",\"password\":\"\"}")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithNotExsitedId() throws Exception {
        mockMvc.perform(
          patch("/users/{id}", NOT_EXISTED_ID)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\":\"TEST\",\"password\":\"TEST\"}")
        )
                .andExpect(status().isNotFound());

        verify(userService)
                .updateUser(eq(NOT_EXISTED_ID), any(UserModificationData.class));
    }

    @Test
    void destroyWithExistedId() throws Exception {
        mockMvc.perform(
                delete("/users/{id}", EXISTED_ID))
                .andExpect(status().isOk());

        verify(userService).deleteUser(EXISTED_ID);
    }

    @Test
    void destroyWithNotExistedId() throws Exception {
        mockMvc.perform(
                delete("/users/{id}", NOT_EXISTED_ID))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(NOT_EXISTED_ID);
    }


}