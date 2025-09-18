package com.cognizant.ecommerce.dto.address;

import lombok.*;


import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private boolean isDefault; // Ensure this field exists and is a primitive boolean
    private Date createdAt;
    private Date updatedAt;

    // A getter method for the boolean field `isDefault`
    public boolean isDefault() {
        return this.isDefault;
    }

    // You may also have a traditional getter, but the boolean-specific one is important
    public boolean getIsDefault() {
        return this.isDefault;
    }

    // Setter for the boolean field
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}