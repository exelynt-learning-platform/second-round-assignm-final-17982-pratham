package com.ecommerce.productcart.service;

import com.ecommerce.productcart.dto.CartDto;
import com.ecommerce.productcart.dto.CartItemDto;
import com.ecommerce.productcart.model.*;
import com.ecommerce.productcart.repository.CartItemRepository;
import com.ecommerce.productcart.repository.CartRepository;
import com.ecommerce.productcart.repository.ProductRepository;
import com.ecommerce.productcart.exception.InsufficientStockException;
import com.ecommerce.productcart.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, 
                       CartItemRepository cartItemRepository, 
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).items(new java.util.HashSet<>()).build();
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public CartDto addItemToCart(User user, Long productId, Integer quantity) {
        Cart cart = getCartByUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock available");
        }

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

        cartRepository.save(cart);
        return getCartDto(user);
    }

    @Transactional
    public CartDto updateItemQuantity(User user, Long productId, Integer quantity) {
        Cart cart = getCartByUser(user);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));

        if (quantity <= 0) {
            cart.removeItem(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        cartRepository.save(cart);
        return getCartDto(user);
    }

    @Transactional
    public CartDto removeItemFromCart(User user, Long productId) {
        Cart cart = getCartByUser(user);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));

        cart.removeItem(cartItem);

        cartRepository.save(cart);
        return getCartDto(user);
    }

    public CartDto getCartDto(User user) {
        Cart cart = getCartByUser(user);
        return convertToDto(cart);
    }

    private CartDto convertToDto(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(item -> CartItemDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = itemDtos.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDto.builder()
                .items(itemDtos)
                .totalPrice(total)
                .build();
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
