package com.cognizant.ecommerce.dto.product;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonProperty;
=======
>>>>>>> d894a8dc0c82e3b5bfd10bfc2ec9ee207806ca54
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
<<<<<<< HEAD
@AllArgsConstructor
@NoArgsConstructor
=======
@NoArgsConstructor
@AllArgsConstructor
>>>>>>> d894a8dc0c82e3b5bfd10bfc2ec9ee207806ca54
public class ProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private String image_url;
    @JsonProperty("is_active")
    private boolean isActive;
    private Long categoryId;
}
