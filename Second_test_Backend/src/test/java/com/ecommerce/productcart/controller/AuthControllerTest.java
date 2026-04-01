package com.ecommerce.productcart.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.backend.productcart.controller.AuthController;
import com.backend.productcart.dto.RegisterRequest;
import com.backend.productcart.model.Cart;
import com.backend.productcart.model.Role;
import com.backend.productcart.model.User;
import com.backend.productcart.repository.CartRepository;
import com.backend.productcart.repository.RoleRepository;
import com.backend.productcart.repository.UserRepository;
import com.backend.productcart.security.JwtUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("testuser@exelynt.com");
        registerRequest.setPassword("password123");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@exelynt.com")).thenReturn(false);
        when(roleRepository.findByName(Role.RoleType.ROLE_USER))
                .thenReturn(Optional.of(Role.builder().name(Role.RoleType.ROLE_USER).build()));
        when(encoder.encode("password123")).thenReturn("encodedPassword");
        
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ResponseEntity<?> response = authController.registerUser(registerRequest);

        assertEquals(200, response.getStatusCode().value());
        verify(userRepository, times(1)).save(any(User.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testRegisterUser_UsernameTaken() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        ResponseEntity<?> response = authController.registerUser(registerRequest);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testRegisterUser_EmailTaken() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@exelynt.com")).thenReturn(true);
        ResponseEntity<?> response = authController.registerUser(registerRequest);
        assertEquals(400, response.getStatusCode().value());
    }
}
