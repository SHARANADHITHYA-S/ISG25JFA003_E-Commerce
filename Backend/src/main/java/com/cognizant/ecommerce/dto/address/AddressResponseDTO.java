package com.cognizant.ecommerce.dto.address;

import com.cognizant.ecommerce.model.Address;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO extends Address {
    private Long addressId;
    private String street;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phone;
    private boolean isDefault;
    private Date createdAt;
    private Date updatedAt;
    private String addressLine1;
    private String postalCode;

    public AddressResponseDTO(Long id, Object addressLine1, Object addressLine2, String city, String country, boolean aDefault, String phone, Object postalCode, String state) {
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}