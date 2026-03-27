package com.ecommerce.productcart.repository;

import com.ecommerce.productcart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_items WHERE cart_id = :cartId", nativeQuery = true)
    void deleteByCartIdNative(@org.springframework.data.repository.query.Param("cartId") Long cartId);
}
