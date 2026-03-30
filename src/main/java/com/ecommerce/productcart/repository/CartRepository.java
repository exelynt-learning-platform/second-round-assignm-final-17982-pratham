package com.ecommerce.productcart.repository;

import com.ecommerce.productcart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
