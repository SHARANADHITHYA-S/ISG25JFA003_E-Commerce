package com.cognizant.ecommerce.admin.dto;

/**
 * Request DTO for creating or updating a payment method.
 * Contains sensitive information like the full card number.
 */
public class PaymentMethodRequestDTO {
    private String cardType;
    private String cardNumber;
    private String expirationDate;
    private String cardholderName;
    private boolean isDefault;

    // Getters and setters
    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
