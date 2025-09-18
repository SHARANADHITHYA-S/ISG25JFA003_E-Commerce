package com.cognizant.ecommerce.dto.address;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {
    private String street;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phone;
    private boolean isDefault;
    private String addressLine1;

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public String getPostalCode() {
        String postalCode = new String();
        return postalCode;
    }
}