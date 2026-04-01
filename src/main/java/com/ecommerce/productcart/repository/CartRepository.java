package com.ecommerce.productcart.repository;

import com.ecommerce.productcart.model.Cart;
import com.ecommerce.productcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
