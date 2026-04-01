package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.model.Cart;
import com.ecommerce.productcart.model.User;
import com.ecommerce.productcart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@AuthenticationPrincipal User user, @RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(user, productId, quantity);
        return ResponseEntity.ok("Product added to cart");
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok("Product removed from cart");
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal User user) {
        return cartService.getCartByUser(user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
