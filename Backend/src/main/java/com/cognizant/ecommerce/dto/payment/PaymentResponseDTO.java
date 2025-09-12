package com.cognizant.ecommerce.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentResponseDTO {
    private Long id;
    private String status;
    private BigDecimal amount;
    private Date paymentDate;
    private Long orderId;
//    private PaymentMethodDTO paymentMethod;
}
