package com.cognizant.ecommerce.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodRequestDTO {
    // Getters and setters
    @NotBlank(message = "Card type is required")
    private String cardType;

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Expiration date is required")
    private String expirationDate;

    @NotBlank(message = "Cardholder name is required")
    private String cardholderName;

    private boolean isDefault;

//    public void setCardType(String cardType) {
//        this.cardType = cardType;
//    }
//
//    public void setCardNumber(String cardNumber) {
//        this.cardNumber = cardNumber;
//    }
//
//    public void setExpirationDate(String expirationDate) {
//        this.expirationDate = expirationDate;
//    }
//
//    public void setCardholderName(String cardholderName) {
//        this.cardholderName = cardholderName;
//    }
//
//    public void setDefault(boolean aDefault) {
//        isDefault = aDefault;
//    }
}
