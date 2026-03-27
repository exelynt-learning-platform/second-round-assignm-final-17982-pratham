package com.ecommerce.productcart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecommerce.productcart.model.*;
import com.ecommerce.productcart.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("testuser").build();
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .build();

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);

        Set<CartItem> items = new HashSet<>();
        items.add(item);

        cart = Cart.builder().items(items).build();
    }

    @Test
    void testCreateOrderFromCart_Success() {
        when(cartService.getCartByUser(user)).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order order = orderService.createOrderFromCart(user, "123 Street");

        assertNotNull(order);
        assertEquals(new BigDecimal("200.00"), order.getTotalAmount());
        assertEquals(8, product.getStockQuantity()); // Stock deducted
        verify(cartService).clearCart(cart);
    }

    @Test
    void testCreateOrderFromCart_InsufficientStock() {
        product.setStockQuantity(1); // Only 1 in stock, but cart asks for 2
        when(cartService.getCartByUser(user)).thenReturn(cart);

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrderFromCart(user, "123 Street");
        });
    }
}
