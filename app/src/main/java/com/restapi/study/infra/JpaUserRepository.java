package com.restapi.study.infra;

import com.restapi.study.domain.User;
import com.restapi.study.domain.UserRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaUserRepository
        extends UserRepository, CrudRepository<User, Long> {

    User save(User user);

    boolean existsByEmail(String email);
}
