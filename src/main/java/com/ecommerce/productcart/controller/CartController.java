package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.model.Cart;
import com.ecommerce.productcart.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/create")
    public Cart createCart() {
        return cartService.createCart();
    }

    @PostMapping("/add")
    public Cart addToCart(@RequestParam Long cartId,
                          @RequestParam Long productId,
                          @RequestParam int quantity) {
        return cartService.addToCart(cartId, productId, quantity);
    }
}
