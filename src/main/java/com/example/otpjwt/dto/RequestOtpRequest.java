package com.example.otpjwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RequestOtpRequest {
    @NotBlank
    @Pattern(regexp="\\d{10}", message="Phone number must be 10 digits")
    private String phoneNumber;
}

// This DTO is used in your controller to automatically validate 
// incoming JSON request bodies for OTP requests,
//  ensuring the phone number is present and correctly formatted before processing.