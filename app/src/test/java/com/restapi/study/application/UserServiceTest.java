package com.restapi.study.application;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.restapi.study.domain.User;
import com.restapi.study.domain.UserRepository;
import com.restapi.study.dto.UserModificationData;
import com.restapi.study.dto.UserRegisterData;
import com.restapi.study.exception.UserEmailDuplicateException;
import com.restapi.study.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserServiceTest {

    private static final String EXISTED_EMAIL_ADDRESS = "existed@naver.com";

    private static final Long EXISTED_ID = 1L;

    private static final Long NOT_EXISTED_ID = 1000L;

    private static final Long DELETED_USER_ID = 2000L;

    private UserService userService;

    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();

        userService = new UserService(
                mapper, userRepository);

        given(userRepository.existsByEmail(EXISTED_EMAIL_ADDRESS))
                .willReturn(true);

        given(userRepository.save(any(User.class))).will(invocation -> {
            User source = invocation.getArgument(0);
            return User.builder()
                    .id(13L)
                    .email(source.getEmail())
                    .name(source.getName())
                    .build();
        });

        given(userRepository.findByIdAndDeletedIsFalse(EXISTED_ID))
                .willReturn(Optional.of(
                User.builder()
                        .id(EXISTED_ID)
                        .email(EXISTED_EMAIL_ADDRESS)
                        .name("dyson")
                        .password("qwer1234")
                        .build()));

        given(userRepository.findByIdAndDeletedIsFalse(NOT_EXISTED_ID))
                .willReturn(Optional.empty());

        given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                .willReturn(Optional.empty());

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

    @Test
    void updateUserWithExistedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("UPDATE")
                .password("update1234")
                .build();

        User user = userService.updateUser(EXISTED_ID, modificationData);

        assertThat(user.getId()).isEqualTo(EXISTED_ID);
        assertThat(user.getName()).isEqualTo("UPDATE");
        assertThat(user.getEmail()).isEqualTo(EXISTED_EMAIL_ADDRESS);

        verify(userRepository).findByIdAndDeletedIsFalse(EXISTED_ID);
    }

    @Test
    void updateUserWithNotExistedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("UPDATE")
                .password("update1234")
                .build();

        assertThatThrownBy(() -> userService.updateUser(NOT_EXISTED_ID, modificationData))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(NOT_EXISTED_ID);
    }

    @Test
    void updateUserWithNotDeletedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("UPDATE")
                .password("update1234")
                .build();

        assertThatThrownBy(
                () -> userService.updateUser(DELETED_USER_ID, modificationData)
        )
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }

    @Test
    void deleteUserWithExistedId() {
        User user = userService.deleteUser(EXISTED_ID);

        assertThat(user.getId()).isEqualTo(EXISTED_ID);
        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    void deleteUserWithNotExistedId() {
        assertThatThrownBy(() -> userService.deleteUser(NOT_EXISTED_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(NOT_EXISTED_ID);
    }

    @Test
    void deleteUserWithDeletedId() {
        assertThatThrownBy(() -> userService.deleteUser(DELETED_USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }
}