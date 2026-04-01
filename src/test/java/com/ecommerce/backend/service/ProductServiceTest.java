package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("A powerful laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(10);
        product.setImageUrl("http://example.com/laptop.jpg");
    }

    @Test
    void getAllProducts_ReturnsListOfProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
    }

    @Test
    void getProductById_Found_ReturnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    void getProductById_NotFound_ReturnsEmpty() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void createProduct_SavesAndReturnsProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_Success_ReturnsUpdatedProduct() {
        Product updatedDetails = new Product();
        updatedDetails.setName("Gaming Laptop");
        updatedDetails.setDescription("High performance");
        updatedDetails.setPrice(new BigDecimal("1499.99"));
        updatedDetails.setStockQuantity(5);
        updatedDetails.setImageUrl("http://example.com/gaming-laptop.jpg");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct(1L, updatedDetails);

        assertEquals("Gaming Laptop", result.getName());
        assertEquals(new BigDecimal("1499.99"), result.getPrice());
        assertEquals(5, result.getStockQuantity());
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.updateProduct(99L, product));

        assertTrue(ex.getMessage().contains("Product not found with id 99"));
    }

    @Test
    void deleteProduct_CallsRepositoryDelete() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }
}
