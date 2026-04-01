package com.ecommerce.productcart.config;

import com.ecommerce.productcart.model.Product;
import com.ecommerce.productcart.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedConfig {

    @Bean
    CommandLineRunner init(ProductRepository productRepository) {
        return args -> {
            productRepository.save(new Product("Laptop", "High-performance laptop", 50000.0, 10, "laptop.jpg"));
            productRepository.save(new Product("Phone", "Latest smartphone", 20000.0, 20, "phone.jpg"));
            productRepository.save(new Product("Headphones", "Noise-cancelling headphones", 2000.0, 50, "headphones.jpg"));

            System.out.println("Sample products added!");
        };
    }
}
