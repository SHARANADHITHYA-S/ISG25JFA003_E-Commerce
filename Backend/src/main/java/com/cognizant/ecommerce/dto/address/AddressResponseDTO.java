package com.cognizant.ecommerce.dto.address;

import com.cognizant.ecommerce.model.Address;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO extends Address {
    private Long addressId;
    private String address_line2;
    private String city;
    private String state;
    private String postal_code;
    private String country;
    private String phone;
    private boolean isDefault;
    private Long user_id;
    private Date created_at;
    private Date updated_at;
    private String address_line1;




}