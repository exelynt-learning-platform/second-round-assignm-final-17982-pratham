package com.ecommerce.productcart.dto;

import com.ecommerce.productcart.model.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private List<OrderItemDto> items;
    private String clientSecret;

    @Getter
    @Setter
    @Builder
    public static class OrderItemDto {
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}
