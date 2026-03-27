package com.ecommerce.productcart.config;

import com.ecommerce.productcart.model.Role;
import com.ecommerce.productcart.model.Product;
import com.ecommerce.productcart.repository.RoleRepository;
import com.ecommerce.productcart.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;

@Configuration
public class SeedConfig {
    private static final Logger logger = LoggerFactory.getLogger(SeedConfig.class);

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository, ProductRepository productRepository) {
        return args -> {
            // Seed Roles with explicit logging
            if (roleRepository.count() == 0) {
                roleRepository.save(Role.builder().name(Role.RoleType.ROLE_USER).build());
                roleRepository.save(Role.builder().name(Role.RoleType.ROLE_ADMIN).build());
                logger.info("Roles initialized: USER and ADMIN seeded in database.");
            } else {
                logger.info("Roles already exist. Skipping seed.");
            }

            // Seed Products
            productRepository.deleteAll(); // Force refresh to get new image links
            productRepository.saveAll(List.of(
                    Product.builder()
                        .name("Premium Noise Cancelling Headphones")
                        .description("Active noise cancelling with 30-hour battery life and stunning clarity.")
                        .price(new BigDecimal("299.99"))
                        .stockQuantity(50)
                        .imageUrl("/image/c-d-x-PDX_a_82obo-unsplash.jpg")
                        .build(),
                    Product.builder()
                        .name("Smart Fitness Watch Pro")
                        .description("Track your health, sleep, and performance with precision and style.")
                        .price(new BigDecimal("149.50"))
                        .stockQuantity(100)
                        .imageUrl("/image/top-view-storage-devices-glasses.jpg")
                        .build(),
                    Product.builder()
                        .name("Ultra HD Action Camera 4K")
                        .description("Perfect for your adventures with waterproof casing and image stabilization.")
                        .price(new BigDecimal("399.00"))
                        .stockQuantity(30)
                        .imageUrl("/image/camera-equipment-capturing-single-macro-object-generative-ai.jpg")
                        .build(),
                    Product.builder()
                        .name("Minimalist Leather Wallet")
                        .description("Crafted with genuine full-grain leather, slim and RFID protected.")
                        .price(new BigDecimal("89.99"))
                        .stockQuantity(75)
                        .imageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&q=80&w=400")
                        .build(),
                    Product.builder()
                        .name("Ergonomic Mesh Office Chair")
                        .description("Work comfortably with adjustable lumbar support and premium build.")
                        .price(new BigDecimal("499.99"))
                        .stockQuantity(20)
                        .imageUrl("/image/istockphoto-2263668547-1024x1024.jpg")
                        .build()
                ));
        };
    }
}
