package com.restapi.study.application;

import com.restapi.study.domain.User;
import com.restapi.study.domain.UserRepository;
import com.restapi.study.exception.InvalidTokenException;
import com.restapi.study.exception.LoginFailException;
import com.restapi.study.global.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthenticationServiceTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private static final String VALID_EMAIL = "valid@gmail.com";
    private static final String INVALID_EMAIL = "invalid@gmail.com";

    private static final String VALID_PASSWORD = "valid1234";
    private static final String INVALID_PASSWORD = "invalid1234";

    private AuthenticationService authenticationService;

    private UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        JwtUtil JwtUtil = new JwtUtil(SECRET);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        authenticationService = new AuthenticationService(
                JwtUtil, userRepository, passwordEncoder);

        User user = User.builder().id(1L).build();
        user.changePassword(VALID_PASSWORD, passwordEncoder);


        given(userRepository.findByEmail(VALID_EMAIL))
                .willReturn(Optional.of(user));
    }

    @Test
    void loginWithRightEmailAndPassword() {
        String accessToken = authenticationService.login(
                VALID_EMAIL, VALID_PASSWORD);

        assertThat(accessToken).isEqualTo(VALID_TOKEN);

        verify(userRepository).findByEmail(VALID_EMAIL);
    }

    @Test
    void loginWithWrongEmail() {
        assertThatThrownBy(() ->
                authenticationService.login(INVALID_EMAIL, VALID_PASSWORD)
        ).isInstanceOf(LoginFailException.class);

        verify(userRepository).findByEmail(INVALID_EMAIL);
    }

    @Test
    void loginWithWrongPassword() {
        assertThatThrownBy(() ->
                authenticationService.login(VALID_EMAIL, INVALID_PASSWORD)
        ).isInstanceOf(LoginFailException.class);

        verify(userRepository).findByEmail(VALID_EMAIL);
    }



    @Test
    void parseTokenWithValidToken() {
        Long userId = authenticationService.parseToken(VALID_TOKEN);

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void parseTokenWithInvalidToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(INVALID_TOKEN)
        ).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void parseTokenWithEmptyToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(null)
        ).isInstanceOf(InvalidTokenException.class);

        assertThatThrownBy(
                () -> authenticationService.parseToken("")
        ).isInstanceOf(InvalidTokenException.class);
    }
}