package com.backend.productcart.service;

import com.backend.productcart.model.*;
import com.backend.productcart.repository.CartItemRepository;
import com.backend.productcart.repository.CartRepository;
import com.backend.productcart.repository.ProductRepository;
import com.backend.productcart.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, 
                       CartItemRepository cartItemRepository, 
                       ProductRepository productRepository, 
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).items(new java.util.HashSet<>()).build();
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addItemToCart(User user, Long productId, Integer quantity) {
        Cart cart = getCartByUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.addItem(cartItem);
            cartItemRepository.save(cartItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItemQuantity(User user, Long productId, Integer quantity) {
        Cart cart = getCartByUser(user);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not in cart"));

        if (quantity <= 0) {
            cart.removeItem(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(User user, Long productId) {
        Cart cart = getCartByUser(user);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not in cart"));

        cart.removeItem(cartItem);

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Cart cart) {
        if (cart == null || cart.getId() == null) {
            return;
        }
        if (cart.getItems() != null) {
            cart.getItems().clear();
        }
        // Hibernate persists changes automatically via @Transactional
    }
}
