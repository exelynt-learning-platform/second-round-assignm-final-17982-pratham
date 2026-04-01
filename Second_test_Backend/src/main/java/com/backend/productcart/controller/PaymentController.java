package com.backend.productcart.controller;

import com.backend.productcart.model.Order;
import com.backend.productcart.service.OrderService;
import com.backend.productcart.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/create-payment-intent/{orderId}")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(order);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/confirm-payment/{orderId}")
    public ResponseEntity<?> confirmPayment(@PathVariable Long orderId) {
        // In a real app, use a Stripe Webhook for this!
        // This is a simple placeholder to simulate status update after manual confirmation.
        orderService.updateOrderStatus(orderId, Order.OrderStatus.PAID);
        return ResponseEntity.ok().build();
    }
}
