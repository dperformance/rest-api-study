package com.restapi.study.dto;

import com.github.dozermapper.core.Mapping;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class UserRegisterData {
    @NotBlank
    @Mapping("email")
    private String email;

    @NotBlank
    @Mapping("password")
    private String password;

    @NotBlank
    @Mapping("name")
    private String name;

    @Builder
    public UserRegisterData(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
