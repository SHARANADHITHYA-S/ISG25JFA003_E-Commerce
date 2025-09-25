package com.cognizant.ecommerce.dto.order;

import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotNull(message = "Enter user id")
    private Long userid;

    @NotNull(message = "Enter address id")
    private Long addressId;

    @NotNull(message = "Enter valid payment method id")
    private Long paymentMethodId;
}
