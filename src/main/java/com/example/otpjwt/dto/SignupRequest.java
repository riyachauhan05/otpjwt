package com.example.otpjwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp="\\d{10}", message="Phone number must be 10 digits")
    private String phoneNumber;
}

// Validates incoming signup requests to make sure the user provides a 
// non-empty full name and a correctly formatted 10-digit phone number 
// before processing the signup logic.