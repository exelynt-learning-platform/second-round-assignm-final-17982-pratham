package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.dto.CartDto;
import com.ecommerce.productcart.service.CartService;
import com.ecommerce.productcart.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
public class CartController {

    private final CartService cartService;
    private final SecurityUtils securityUtils;

    public CartController(CartService cartService, SecurityUtils securityUtils) {
        this.cartService = cartService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        return ResponseEntity.ok(cartService.getCartDto(securityUtils.getCurrentUser()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto> addItemToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addItemToCart(securityUtils.getCurrentUser(), productId, quantity));
    }

    @PutMapping("/update")
    public ResponseEntity<CartDto> updateItemQuantity(@RequestParam Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(securityUtils.getCurrentUser(), productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeItemFromCart(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(securityUtils.getCurrentUser(), productId));
    }
}
