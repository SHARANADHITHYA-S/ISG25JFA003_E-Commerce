package com.cognizant.ecommerce.exception;

public class BadCredentialsException extends RuntimeException{
    public BadCredentialsException(String message){
        super(message);
    }
}
