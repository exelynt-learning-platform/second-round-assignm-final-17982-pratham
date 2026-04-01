package com.ecommerce.productcart;

import com.ecommerce.productcart.model.Product;
import com.ecommerce.productcart.model.User;
import com.ecommerce.productcart.repository.*;
import com.ecommerce.productcart.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ECommerceBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Seed some data
        productRepository.save(new Product("Laptop", "Description", 500.0, 10, "image.jpg"));

        User user = new User("testuser", passwordEncoder.encode("password"), "ROLE_USER");
        userRepository.save(user);

        token = "Bearer " + jwtUtil.generateToken("testuser");
    }

    @Test
    void testUserRegistration() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .param("username", "newuser")
                .param("password", "password")
                .param("role", "ROLE_USER"))
                .andExpect(status().isOk());
    }

    @Test
    void testProductManagement() throws Exception {
        mockMvc.perform(post("/api/products/add")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Laptop\", \"description\":\"High-end laptop\", \"price\":1200.0, \"stockQuantity\":10, \"imageUrl\":\"laptop.jpg\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products/all")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void testCartOperations() throws Exception {
        Product product = new Product("Phone", "Smartphone", 800.0, 20, "phone.jpg");
        product = productRepository.save(product);

        mockMvc.perform(post("/api/cart/add")
                .header("Authorization", token)
                .param("productId", product.getId().toString())
                .param("quantity", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void testOrderPlacement() throws Exception {
        Product product = new Product("Tablet", "Graphic tablet", 500.0, 15, "tablet.jpg");
        product = productRepository.save(product);

        mockMvc.perform(post("/api/cart/add")
                .header("Authorization", token)
                .param("productId", product.getId().toString())
                .param("quantity", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/orders/checkout")
                .header("Authorization", token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}
