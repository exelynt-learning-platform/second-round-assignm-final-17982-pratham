package com.ecommerce.backend.controller;

import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.payload.response.CheckoutResponse;
import com.ecommerce.backend.service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(Authentication authentication, @RequestParam String shippingAddress) {
        try {
            String username = authentication.getName();
            CheckoutResponse response = orderService.createOrderFromCart(username, shippingAddress);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Payment Error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.getUserOrders(username));
    }

    @PostMapping("/{orderId}/payment-status")
    public ResponseEntity<Order> updatePaymentStatus(@PathVariable Long orderId, @RequestParam String status) {
        // In a real app, this should be a webhook secured by Stripe signature
        // verification.
        return ResponseEntity.ok(orderService.updatePaymentStatus(orderId, status));
    }
}
