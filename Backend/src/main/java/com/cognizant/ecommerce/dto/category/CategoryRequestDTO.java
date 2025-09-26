package com.cognizant.ecommerce.dto.category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "Add a name for the category")
    private String name;

    @NotBlank(message = "Add a description for the category")
    private String description;
}