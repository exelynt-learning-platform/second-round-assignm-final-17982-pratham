package com.backend.productcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.productcart.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
