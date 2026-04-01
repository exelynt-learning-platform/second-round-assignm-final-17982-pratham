package com.ecommerce.productcart.service;

import com.ecommerce.productcart.model.Cart;
import com.ecommerce.productcart.model.CartItem;
import com.ecommerce.productcart.model.Product;
import com.ecommerce.productcart.model.User;
import com.ecommerce.productcart.repository.CartItemRepository;
import com.ecommerce.productcart.repository.CartRepository;
import com.ecommerce.productcart.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public Optional<Cart> getCartByUser(User user) {
        return cartRepository.findByUser(user);
    }
}
