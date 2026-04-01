package com.backend.productcart.controller;

import com.backend.productcart.dto.OrderRequest;
import com.backend.productcart.dto.OrderResponse;
import com.backend.productcart.model.Order;
import com.backend.productcart.service.OrderService;
import com.backend.productcart.service.PaymentService;
import com.backend.productcart.util.SecurityUtils;

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
    private final PaymentService paymentService;
    private final SecurityUtils securityUtils;

    public OrderController(OrderService orderService, PaymentService paymentService, SecurityUtils securityUtils) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.createOrderFromCart(securityUtils.getCurrentUser(), request.getShippingAddress());
        
        try {
            var paymentIntent = paymentService.createPaymentIntent(order);
            OrderResponse response = convertToDto(order);
            response.setClientSecret(paymentIntent.getClientSecret());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public List<OrderResponse> getUserOrders() {
        return orderService.getOrdersByUser(securityUtils.getCurrentUser()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        // Authorization check
        if (!order.getUser().getId().equals(securityUtils.getCurrentUser().getId())) {
             return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(convertToDto(order));
    }

    private OrderResponse convertToDto(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .shippingAddress(order.getShippingAddress())
                .items(order.getItems().stream()
                        .map(item -> OrderResponse.OrderItemDto.builder()
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .price(item.getPriceAtPurchase())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
