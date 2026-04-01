package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.model.Order;
import com.ecommerce.productcart.model.User;
import com.ecommerce.productcart.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
