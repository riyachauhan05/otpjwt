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
