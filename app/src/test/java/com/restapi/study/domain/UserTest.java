package com.restapi.study.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }
    @Test
    void changeWith() {
        User user = User.builder().build();

        user.changeWith(User.builder()
                .name("TEST")
                .password("TEST")
                .build());

        assertThat(user.getName()).isEqualTo("TEST");
        assertThat(user.getPassword()).isNotEqualTo("");
    }

    @Test
    void changePassword() {
        User user = User.builder().build();

        user.changePassword("TEST", passwordEncoder);

        assertThat(user.getPassword()).isNotEmpty();
    }

    @Test
    void destroy() {
        User user = User.builder().build();

        assertThat(user.isDeleted()).isFalse();

        user.destroy();

        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    void authenticate() {
        User user = User.builder().build();
        user.changePassword("valid1234",passwordEncoder);

        assertThat(user.authenticate("valid1234", passwordEncoder)).isTrue();
        assertThat(user.authenticate("invalid1234", passwordEncoder)).isFalse();
    }

    @Test
    void authenticateWithDeletedUser() {
        User user = User.builder()
                .deleted(true)
                .build();

        assertThat(user.authenticate("valid1234", passwordEncoder)).isFalse();
        assertThat(user.authenticate("invalid1234", passwordEncoder)).isFalse();
    }
}