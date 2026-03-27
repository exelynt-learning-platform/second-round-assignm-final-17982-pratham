package com.ecommerce.productcart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecommerce.productcart.model.*;
import com.ecommerce.productcart.repository.CartItemRepository;
import com.ecommerce.productcart.repository.CartRepository;
import com.ecommerce.productcart.repository.ProductRepository;
import com.ecommerce.productcart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

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
        user = User.builder().id(1L).username("testuser").build();
        product = Product.builder().id(1L).name("Test Product").price(new BigDecimal("100.00")).stockQuantity(10).build();
        cart = Cart.builder().id(1L).user(user).items(new HashSet<>()).build();
    }

    @Test
    void testGetCartByUser_Exists() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        Cart result = cartService.getCartByUser(user);
        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
    }

    @Test
    void testGetCartByUser_NewCartCreated() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);
        
        Cart result = cartService.getCartByUser(user);
        assertNotNull(result);
        assertEquals(user, result.getUser());
    }

    @Test
    void testAddItemToCart_NewItem() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.addItemToCart(user, 1L, 2);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testUpdateItemQuantity() {
        CartItem cartItem = CartItem.builder().id(1L).cart(cart).product(product).quantity(1).build();
        cart.addItem(cartItem);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.updateItemQuantity(user, 1L, 5);

        assertNotNull(result);
        assertEquals(5, cart.getItems().iterator().next().getQuantity());
        verify(cartItemRepository, times(1)).save(cartItem);
    }
    
    @Test
    void testClearCart() {
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);
        
        cartService.clearCart(cart);
        
        assertTrue(cart.getItems().isEmpty());
    }
}
