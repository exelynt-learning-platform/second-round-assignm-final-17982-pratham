package com.ecommerce.productcart.service;

import static org.junit.jupiter.api.Assertions.*;

import com.ecommerce.productcart.model.Cart;
import com.ecommerce.productcart.model.CartItem;
import com.ecommerce.productcart.model.Product;
import com.ecommerce.productcart.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testCalculateOrderAmount() throws Exception {
        User user = User.builder().id(1L).build();
        Product product1 = Product.builder().id(1L).price(new BigDecimal("10.50")).build();
        Product product2 = Product.builder().id(2L).price(new BigDecimal("20.00")).build();

        CartItem item1 = CartItem.builder().id(1L).product(product1).quantity(2).build();
        CartItem item2 = CartItem.builder().id(2L).product(product2).quantity(1).build();

        Set<CartItem> items = new HashSet<>();
        items.add(item1);
        items.add(item2);

        Cart cart = Cart.builder().id(1L).user(user).items(items).build();

        // 2 * 10.50 + 20.00 = 41.00 dollars -> 4100 cents
        long calculateAmount = (long) (paymentService.getClass()
                .getDeclaredMethod("calculateOrderAmount", Cart.class)
                .invoke(paymentService, cart));

        assertEquals(4100L, calculateAmount);
    }
}
