package com.restapi.study.application;

import com.restapi.study.domain.User;
import com.restapi.study.domain.UserRepository;
import com.restapi.study.exception.LoginFailException;
import com.restapi.study.global.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(JwtUtil jwtUtil,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder)
    {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email,
                        String password)
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new LoginFailException(email));

        if (!user.authenticate(password, passwordEncoder)) {
            throw new LoginFailException(email);
        }

        return jwtUtil.encode(user.getId());
    }

    public Long parseToken(String accessToken) {
        Claims claims = jwtUtil.decode(accessToken);
        return claims.get("userId", Long.class);
    }
}
