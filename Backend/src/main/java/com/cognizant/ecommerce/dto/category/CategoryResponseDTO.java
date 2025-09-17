package com.cognizant.ecommerce.dto.category;

import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private List<ProductResponseDTO> products;
}