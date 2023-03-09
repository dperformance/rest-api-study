package com.restapi.study.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String password;

    private String name;

    private boolean deleted = false;

    @Builder
    public User(Long id, String email, String password, String name, boolean deleted) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.deleted = deleted;
    }

    public void changeWith(User source) {
        this.name = source.getName();
        this.password = source.getPassword();
    }

    public void destroy() {
        deleted = true;
    }

    public boolean authenticate(String password,
                                PasswordEncoder passwordEncoder) {
        return !deleted && passwordEncoder.matches(password, this.password);
    }

    public void changePassword(String password,
                               PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
}
