package com.backend.productcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.productcart.model.Cart;
import com.backend.productcart.model.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
