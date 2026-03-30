package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.model.Order;
import com.ecommerce.productcart.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public Order placeOrder(@RequestParam Long cartId) {
        return orderService.placeOrder(cartId);
    }
}
