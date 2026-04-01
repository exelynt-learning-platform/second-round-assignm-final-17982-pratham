package com.ecommerce.productcart.config;

import com.ecommerce.productcart.model.Product;
import com.ecommerce.productcart.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
public class SeedConfig {

    @Bean
    CommandLineRunner init(ProductRepository productRepository) {
        return args -> {

            productRepository.save(new Product("Laptop", 50000, 10));
            productRepository.save(new Product("Phone", 20000, 20));
            productRepository.save(new Product("Headphones", 2000, 50));

            System.out.println("Sample products added!");
        };
    }
}
