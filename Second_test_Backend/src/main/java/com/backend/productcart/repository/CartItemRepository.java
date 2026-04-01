package com.backend.productcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.backend.productcart.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
