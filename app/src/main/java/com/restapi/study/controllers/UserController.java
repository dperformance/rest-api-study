package com.restapi.study.controllers;

import com.restapi.study.application.UserService;
import com.restapi.study.domain.User;
import com.restapi.study.dto.UserRegisterData;
import com.restapi.study.dto.UserResultData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 1. 회원가입 -> POST /users UserRequestData (email이 unique key!)
 * 2. 회원목록, 상세보기 -> admin
 * 3. 사용자 정보 갱신 -> PATCH /users/{id}
 * 4. 회원 탈퇴 -> DELETE /users/{id}
 * id
 * email
 * password
 * name
 * phone
 */

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController (
            UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResultData create(
            @RequestBody @Valid UserRegisterData userRegisterData)
    {
        User user = userService.registerUser(userRegisterData);
        return getUserResultData(user);
    }

    private UserResultData getUserResultData(User user) {
        return UserResultData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();

    }
}
