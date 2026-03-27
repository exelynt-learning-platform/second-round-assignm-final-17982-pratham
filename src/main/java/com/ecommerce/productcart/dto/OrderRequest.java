package com.ecommerce.productcart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    @NotBlank
    private String shippingAddress;
}
