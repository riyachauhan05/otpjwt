package com.example.otpjwt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
   @NotBlank(message = "OTP is required")
    private String otp;
}
