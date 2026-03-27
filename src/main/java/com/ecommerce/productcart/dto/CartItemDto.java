package com.ecommerce.productcart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CartItemDto {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}
