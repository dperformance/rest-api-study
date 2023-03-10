package com.restapi.study.infra;

import com.restapi.study.domain.Role;
import com.restapi.study.domain.RoleRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaRoleRepository
        extends RoleRepository, CrudRepository<Role, Long> {

    List<Role> findAllByUserId(Long userId);

    Role save(Role role);
}
