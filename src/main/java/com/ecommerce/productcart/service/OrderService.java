package com.ecommerce.productcart.service;

import com.ecommerce.productcart.model.Cart;
import com.ecommerce.productcart.model.Order;
import com.ecommerce.productcart.model.OrderItem;
import com.ecommerce.productcart.model.User;
import com.ecommerce.productcart.repository.CartItemRepository;
import com.ecommerce.productcart.repository.CartRepository;
import com.ecommerce.productcart.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(CartRepository cartRepository, OrderRepository orderRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public Order placeOrder(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum());

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        order.setStatus("PAID");
        Order savedOrder = orderRepository.save(order);

        // Clear cart items
        cartItemRepository.deleteAll(cart.getItems());

        return savedOrder;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
