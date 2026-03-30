package com.ecommerce.productcart.repository;

import com.ecommerce.productcart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
