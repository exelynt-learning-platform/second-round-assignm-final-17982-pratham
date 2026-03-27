package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.dto.CartDto;
import com.ecommerce.productcart.dto.CartItemDto;
import com.ecommerce.productcart.model.Cart;
import com.ecommerce.productcart.model.User;
import com.ecommerce.productcart.repository.UserRepository;
import com.ecommerce.productcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        Cart cart = cartService.getCartByUser(getCurrentUser());
        return ResponseEntity.ok(convertToDto(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto> addItemToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        Cart cart = cartService.addItemToCart(getCurrentUser(), productId, quantity);
        return ResponseEntity.ok(convertToDto(cart));
    }

    @PutMapping("/update")
    public ResponseEntity<CartDto> updateItemQuantity(@RequestParam Long productId, @RequestParam Integer quantity) {
        Cart cart = cartService.updateItemQuantity(getCurrentUser(), productId, quantity);
        return ResponseEntity.ok(convertToDto(cart));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeItemFromCart(@PathVariable Long productId) {
        Cart cart = cartService.removeItemFromCart(getCurrentUser(), productId);
        return ResponseEntity.ok(convertToDto(cart));
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
}
