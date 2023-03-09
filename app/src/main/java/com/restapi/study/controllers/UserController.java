package com.restapi.study.controllers;

import com.restapi.study.application.UserService;
import com.restapi.study.domain.User;
import com.restapi.study.dto.UserModificationData;
import com.restapi.study.dto.UserRegisterData;
import com.restapi.study.dto.UserResultData;
import com.restapi.study.security.UserAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;

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

    @PatchMapping("{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public UserResultData update(
            @PathVariable Long id,
            @RequestBody @Valid UserModificationData userModificationData,
            UserAuthentication authentication) throws AccessDeniedException {
        Long userId = authentication.getUserId();
        User user = userService.updateUser(id ,userModificationData, userId);

        return getUserResultData(user);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);

    }

    private UserResultData getUserResultData(User user) {
        if (user == null) {
            return null;
        }

        return UserResultData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();

    }
}
