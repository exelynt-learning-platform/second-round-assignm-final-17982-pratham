package com.ecommerce.productcart.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;
import com.ecommerce.productcart.model.Order;
import com.ecommerce.productcart.model.Cart;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
}
