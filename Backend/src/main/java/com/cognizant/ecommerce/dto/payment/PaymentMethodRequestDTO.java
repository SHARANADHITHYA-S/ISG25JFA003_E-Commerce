package com.cognizant.ecommerce.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMethodRequestDTO {
    private String type;     // e.g., "Credit Card", "UPI"
    private String provider; // e.g., "Visa", "Google Pay"
}
