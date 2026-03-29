package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.CartItem;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "stripeSecretKey", "sk_test_mock");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("50.00"));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);
        cart.getItems().add(item);
    }

    @Test
    void testCreateOrderFromCart_EmptyCart() {
        cart.getItems().clear();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrderFromCart("testuser", "123 Test St");
        });

        assertEquals("Cannot create order from an empty cart", exception.getMessage());
    }

    @Test
    void testCreateOrderFromCart_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrderFromCart("unknown", "123 Test St"));

        assertEquals("User not found", ex.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrderFromCart_CartNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrderFromCart("testuser", "123 Test St"));

        assertEquals("Cart not found", ex.getMessage());
    }

    @Test
    void testGetUserOrders_ReturnsOrderList() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalPrice(new BigDecimal("100.00"));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(order));

        List<Order> orders = orderService.getUserOrders("testuser");

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(new BigDecimal("100.00"), orders.get(0).getTotalPrice());
    }

    @Test
    void testGetUserOrders_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> orderService.getUserOrders("unknown"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testUpdatePaymentStatus_Success() {
        Order order = new Order();
        order.setId(1L);
        order.setPaymentStatus("PENDING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.updatePaymentStatus(1L, "PAID");

        assertEquals("PAID", result.getPaymentStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testUpdatePaymentStatus_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> orderService.updatePaymentStatus(99L, "PAID"));

        assertEquals("Order not found", ex.getMessage());
    }
}
