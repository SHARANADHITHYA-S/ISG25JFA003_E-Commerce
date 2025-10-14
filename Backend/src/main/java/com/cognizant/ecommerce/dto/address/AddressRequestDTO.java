package com.cognizant.ecommerce.dto.address;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {
    @NotBlank(message = "Street cannot be null")
    private String street;

    @NotBlank(message = "address cannot be null")
    private String address_line1;

    private String address_line2;

    @NotBlank(message = "city cannot be null")
    private String city;

    @NotBlank(message = "state cannot be null")
    private String state;

    @NotBlank(message = "postal code cannot be null")
    private String postal_code;

    @NotBlank(message = "country cannot be null")
    private String country;

    @NotBlank(message = "phone no. cannot be null")
    private String phone;

    private boolean isDefault;




}