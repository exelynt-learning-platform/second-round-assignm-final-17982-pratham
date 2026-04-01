package com.ecommerce.productcart.controller;

import com.ecommerce.productcart.model.Product;
import com.ecommerce.productcart.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/get-product/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}
