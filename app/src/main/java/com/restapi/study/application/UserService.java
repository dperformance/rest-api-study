package com.restapi.study.application;


import com.github.dozermapper.core.Mapper;
import com.restapi.study.domain.User;
import com.restapi.study.domain.UserRepository;
import com.restapi.study.dto.UserModificationData;
import com.restapi.study.dto.UserRegisterData;
import com.restapi.study.exception.UserEmailDuplicateException;
import com.restapi.study.exception.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;

@Service
@Transactional
public class UserService {

    private final Mapper mapper;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService (Mapper dozerMapper,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder)
    {
        this.mapper = dozerMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegisterData userRegisterData) {
        String email = userRegisterData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicateException(email);
        }

        User user = userRepository.save(
                mapper.map(userRegisterData, User.class));

        user.changePassword(userRegisterData.getPassword(), passwordEncoder);

        return user;
    }


    public User updateUser(Long id,
                           UserModificationData userModificationData,
                           Long userId) throws AccessDeniedException {
        if (id != userId) {
            throw new AccessDeniedException("Access Denided");
        }

        User user = findUser(id);

        User source = mapper.map(userModificationData, User.class);

        user.changeWith(source);

        return user;
    }

    public User deleteUser(Long id) {
        User user = findUser(id);

        user.destroy();

        return user;
    }

    private User findUser(Long id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
