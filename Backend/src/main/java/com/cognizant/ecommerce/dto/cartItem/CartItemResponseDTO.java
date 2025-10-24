package com.cognizant.ecommerce.dto.cartItem;

import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long id;
    private Long userId;
    private Long cartId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String image_url;
    private BigDecimal totalPrice;
}
