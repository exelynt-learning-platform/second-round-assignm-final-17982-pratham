package com.ecommerce.productcart.service;

import com.ecommerce.productcart.dto.OrderResponse;
import com.ecommerce.productcart.model.*;
import com.ecommerce.productcart.repository.OrderRepository;
import com.ecommerce.productcart.repository.ProductRepository;
import com.stripe.exception.StripeException;
import com.ecommerce.productcart.exception.ResourceNotFoundException;
import com.ecommerce.productcart.exception.InsufficientStockException;
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
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, 
                        CartService cartService, 
                        ProductRepository productRepository,
                        PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public OrderResponse createOrderFromCart(User user, String shippingAddress) throws StripeException {
        Cart cart = cartService.getCartByUser(user);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new InsufficientStockException("Cannot create order from empty cart");
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
                        throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
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

        // Save
        Order savedOrder = orderRepository.save(order);

        // Process Payment Intent
        var paymentIntent = paymentService.createPaymentIntent(savedOrder);
        
        OrderResponse response = convertToDto(savedOrder);
        response.setClientSecret(paymentIntent.getClientSecret());
        
        return response;
    }

    public OrderResponse convertToDto(Order order) {
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

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
