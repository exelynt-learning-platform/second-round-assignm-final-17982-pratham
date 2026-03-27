package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.dto.OrderRequest;
import com.ecommerce.productcart.dto.OrderResponse;
import com.ecommerce.productcart.model.Order;
import com.ecommerce.productcart.model.User;
import com.ecommerce.productcart.repository.UserRepository;
import com.ecommerce.productcart.service.OrderService;
import com.ecommerce.productcart.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.createOrderFromCart(getCurrentUser(), request.getShippingAddress());
        
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
        return orderService.getOrdersByUser(getCurrentUser()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        // Authorization check
        if (!order.getUser().getId().equals(getCurrentUser().getId())) {
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
