package com.ecommerce.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckoutResponse {
    private String clientSecret;
    private Long orderId;
}
