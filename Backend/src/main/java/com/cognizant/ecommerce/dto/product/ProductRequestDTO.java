package com.cognizant.ecommerce.dto.product;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private String image_url;
    private boolean is_active;
    private Long categoryId;
}
