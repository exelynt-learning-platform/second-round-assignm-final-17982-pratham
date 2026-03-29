package com.ecommerce.backend.controller;

import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.payload.request.CartRequest;
import com.ecommerce.backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addToCart(Authentication authentication, @Valid @RequestBody CartRequest cartRequest) {
        String username = authentication.getName();
        return ResponseEntity
                .ok(cartService.addProductToCart(username, cartRequest.getProductId(), cartRequest.getQuantity()));
    }

    @PutMapping("/items")
    public ResponseEntity<Cart> updateCartItem(Authentication authentication,
            @Valid @RequestBody CartRequest cartRequest) {
        String username = authentication.getName();
        return ResponseEntity
                .ok(cartService.updateCartItem(username, cartRequest.getProductId(), cartRequest.getQuantity()));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeFromCart(Authentication authentication, @PathVariable Long productId) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.removeProductFromCart(username, productId));
    }
}
