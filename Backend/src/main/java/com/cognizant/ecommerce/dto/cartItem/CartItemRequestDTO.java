package com.cognizant.ecommerce.dto.cartItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDTO {

    @NotNull(message = "Add product id")
    private Long productId;

    @NotNull(message = "Enter quantity")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}