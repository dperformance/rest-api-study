package com.restapi.study.application;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.restapi.study.domain.User;
import com.restapi.study.domain.UserRepository;
import com.restapi.study.dto.UserRegisterData;
import com.restapi.study.exception.UserEmailDuplicateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserServiceTest {

    private static final String EXISTED_EMAIL_ADDRESS = "existed@naver.com";

    private UserService userService;
    private final UserRepository userRepository = mock(UserRepository.class);


    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();

        userService = new UserService(
                mapper, userRepository);

        given(userRepository.existsByEmail(EXISTED_EMAIL_ADDRESS))
                .willThrow(new UserEmailDuplicateException(EXISTED_EMAIL_ADDRESS));

        given(userRepository.save(any(User.class))).will(invocation -> {
            User source = invocation.getArgument(0);
            return User.builder()
                    .id(13L)
                    .email(source.getEmail())
                    .name(source.getName())
                    .build();
        });
    }

    @Test
    void registerUser() {
        UserRegisterData userRegisterData = UserRegisterData.builder()
                                                            .email("dyson@naver.com")
                                                            .password("qwer1234")
                                                            .name("dyson")
                                                            .build();


        User user = userService.registerUser(userRegisterData);

        assertThat(user.getId()).isEqualTo(13L);
        assertThat(user.getName()).isEqualTo("dyson");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserWithDuplicatedEmail() {
        UserRegisterData userRegisterData = UserRegisterData.builder()
                .email(EXISTED_EMAIL_ADDRESS)
                .name("dyson")
                .password("qwer1234")
                .build();

        assertThatThrownBy(() -> userService.registerUser(userRegisterData))
                .isInstanceOf(UserEmailDuplicateException.class);

        verify(userRepository).existsByEmail(EXISTED_EMAIL_ADDRESS);
    }
}