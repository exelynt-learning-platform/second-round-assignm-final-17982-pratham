package com.ecommerce.productcart.repository;

import com.ecommerce.productcart.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleType name);
}
