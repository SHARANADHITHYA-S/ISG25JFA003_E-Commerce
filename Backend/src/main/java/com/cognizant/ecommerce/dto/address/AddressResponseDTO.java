package com.cognizant.ecommerce.dto.address;

import lombok.*;


import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    private Long id;
    private String address_line1;
    private String address_line2;
    private String city;
    private String state;
    private String street;
    private String postal_code;
    private String country;
    private String phone;
    private boolean isDefault; // Ensure this field exists and is a primitive boolean
    private Date created_at;
    private Date updated_at;




}