package com.backend.productcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.productcart.model.Order;
import com.backend.productcart.model.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
