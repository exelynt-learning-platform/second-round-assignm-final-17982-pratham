package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.dto.OrderRequest;
import com.ecommerce.productcart.dto.OrderResponse;
import com.ecommerce.productcart.model.Order;
import com.ecommerce.productcart.service.OrderService;
import com.ecommerce.productcart.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
public class OrderController {

    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    public OrderController(OrderService orderService, SecurityUtils securityUtils) {
        this.orderService = orderService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody OrderRequest request) {
        try {
            OrderResponse response = orderService.createOrderFromCart(securityUtils.getCurrentUser(), request.getShippingAddress());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public List<OrderResponse> getUserOrders() {
        return orderService.getOrdersByUser(securityUtils.getCurrentUser()).stream()
                .map(orderService::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        // Authorization check
        if (!order.getUser().getId().equals(securityUtils.getCurrentUser().getId())) {
             return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(orderService.convertToDto(order));
    }
}
