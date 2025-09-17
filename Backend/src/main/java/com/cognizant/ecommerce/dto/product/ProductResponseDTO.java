package com.cognizant.ecommerce.dto.product;

import com.cognizant.ecommerce.dto.category.CategoryResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image_url;
    @JsonProperty("is_active")
    private boolean isActive;
    private Date created_at;
    private Date updated_at;
    private Long category_id;
    private Long quantity;
}