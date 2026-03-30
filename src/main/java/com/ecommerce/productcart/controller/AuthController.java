package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.model.User;
import com.ecommerce.productcart.service.AuthService;
import com.ecommerce.productcart.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                      @RequestParam String password) {
        User user = authService.login(username, password);
        return jwtUtil.generateToken(user.getUsername());
    }
}
