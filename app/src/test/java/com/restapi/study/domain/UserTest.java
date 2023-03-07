package com.restapi.study.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void changeWith() {
        User user = User.builder().build();

        user.changeWith(User.builder()
                .name("TEST")
                .password("qwer1234")
                .build());

        assertThat(user.getName()).isEqualTo("TEST");
        assertThat(user.getPassword()).isEqualTo("qwer1234");
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
        User user = User.builder()
                .password("valid1234")
                .build();

        assertThat(user.authenticate("valid1234")).isTrue();
        assertThat(user.authenticate("invalid1234")).isFalse();
    }

    @Test
    void authenticateWithDeletedUser() {
        User user = User.builder()
                .password("valid1234")
                .deleted(true)
                .build();

        assertThat(user.authenticate("valid1234")).isFalse();
        assertThat(user.authenticate("invalid1234")).isFalse();
    }
}