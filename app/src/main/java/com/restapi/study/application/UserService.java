package com.restapi.study.application;


import com.github.dozermapper.core.Mapper;
import com.restapi.study.domain.User;
import com.restapi.study.domain.UserRepository;
import com.restapi.study.dto.UserRegisterData;
import com.restapi.study.dto.UserResultData;
import com.restapi.study.exception.UserEmailDuplicateException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

    private final Mapper mapper;

    private final UserRepository userRepository;

    public UserService (
            Mapper dozerMapper,
            UserRepository userRepository)
    {
        this.mapper = dozerMapper;
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegisterData userRegisterData) {
        String email = userRegisterData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicateException(email);
        }

        User user = userRepository.save(
                mapper.map(userRegisterData, User.class));

        return user;
    }


}
