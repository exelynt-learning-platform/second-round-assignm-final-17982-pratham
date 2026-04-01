package com.backend.productcart.service;

import com.backend.productcart.model.*;
import com.backend.productcart.repository.OrderRepository;
import com.backend.productcart.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, CartService cartService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrderFromCart(User user, String shippingAddress) {
        Cart cart = cartService.getCartByUser(user);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order from empty cart");
        }

        // Calculate Order Essentials
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .shippingAddress(shippingAddress)
                .totalAmount(total)
                .status(Order.OrderStatus.PENDING)
                .items(new HashSet<>())
                .build();

        Set<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    if (product.getStockQuantity() < cartItem.getQuantity()) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getName());
                    }
                    product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                    productRepository.save(product);

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(cartItem.getQuantity())
                            .priceAtPurchase(product.getPrice())
                            .build();
                })
                .collect(Collectors.toSet());

        order.setItems(orderItems);

        // Process state transition before final save
        cartService.clearCart(cart);

        // Save and return
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
