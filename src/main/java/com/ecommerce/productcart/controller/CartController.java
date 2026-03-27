package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.dto.CartDto;
import com.ecommerce.productcart.dto.CartItemDto;
import com.ecommerce.productcart.model.Cart;
import com.ecommerce.productcart.service.CartService;
import com.ecommerce.productcart.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        Cart cart = cartService.getCartByUser(securityUtils.getCurrentUser());
        return ResponseEntity.ok(convertToDto(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto> addItemToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        Cart cart = cartService.addItemToCart(securityUtils.getCurrentUser(), productId, quantity);
        return ResponseEntity.ok(convertToDto(cart));
    }

    @PutMapping("/update")
    public ResponseEntity<CartDto> updateItemQuantity(@RequestParam Long productId, @RequestParam Integer quantity) {
        Cart cart = cartService.updateItemQuantity(securityUtils.getCurrentUser(), productId, quantity);
        return ResponseEntity.ok(convertToDto(cart));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeItemFromCart(@PathVariable Long productId) {
        Cart cart = cartService.removeItemFromCart(securityUtils.getCurrentUser(), productId);
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
