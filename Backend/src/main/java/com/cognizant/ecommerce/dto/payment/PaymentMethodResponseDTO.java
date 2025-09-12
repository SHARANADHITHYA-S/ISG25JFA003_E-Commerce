package com.cognizant.ecommerce.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMethodResponseDTO {
    private Long id;
    private String type;
    private String provider;
}
