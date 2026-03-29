package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
    }

    @Test
    void testAddProductToCart() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.addProductToCart("testuser", 1L, 2);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetCartByUsername_ExistingCart_ReturnsCart() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartByUsername("testuser");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUser().getUsername());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testGetCartByUsername_NoExistingCart_CreatesNewCart() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.getCartByUsername("testuser");

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testGetCartByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cartService.getCartByUsername("unknown"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testUpdateCartItem_UpdatesQuantity() {
        // Pre-add a product to cart items
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.addProductToCart("testuser", 1L, 2);

        // Now update it
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));
        Cart result = cartService.updateCartItem("testuser", 1L, 5);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity());
    }

    @Test
    void testUpdateCartItem_ZeroQuantity_RemovesItem() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.addProductToCart("testuser", 1L, 3);

        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));
        Cart result = cartService.updateCartItem("testuser", 1L, 0);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testRemoveProductFromCart_RemovesItem() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.addProductToCart("testuser", 1L, 2);

        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));
        Cart result = cartService.removeProductFromCart("testuser", 1L);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testRemoveProductFromCart_ItemNotFound_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cartService.removeProductFromCart("testuser", 99L));

        assertEquals("Item not found in cart", ex.getMessage());
    }
}
