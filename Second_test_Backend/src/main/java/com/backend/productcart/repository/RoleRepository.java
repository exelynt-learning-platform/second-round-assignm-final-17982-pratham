package com.backend.productcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.productcart.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleType name);
}
