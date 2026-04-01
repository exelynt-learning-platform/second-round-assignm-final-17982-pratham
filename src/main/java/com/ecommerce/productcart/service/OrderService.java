package com.ecommerce.productcart.service;

import com.ecommerce.productcart.model.*;
import com.ecommerce.productcart.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public OrderService(CartRepository cartRepository,
                        OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();

        double total = 0;

        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();

            orderItem.setProductName(item.getProduct().getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());

            total += item.getQuantity() * item.getProduct().getPrice();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }
}
