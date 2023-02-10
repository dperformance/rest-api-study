package com.restapi.study.domain;

public interface UserRepository {
    User save(User user);

    boolean existsByEmail(String email);
}
