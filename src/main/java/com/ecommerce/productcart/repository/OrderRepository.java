package com.ecommerce.productcart.repository;

import com.ecommerce.productcart.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
