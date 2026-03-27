package com.ecommerce.productcart.repository;

import com.ecommerce.productcart.model.Order;
import com.ecommerce.productcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
