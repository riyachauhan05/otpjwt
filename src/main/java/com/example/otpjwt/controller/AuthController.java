package com.example.otpjwt.controller;

import com.example.otpjwt.dto.RequestOtpRequest;
import com.example.otpjwt.dto.VerifyOtpRequest;
import com.example.otpjwt.dto.SignupRequest;
import com.example.otpjwt.model.ApiResponse;
import com.example.otpjwt.model.User;
import com.example.otpjwt.service.AuthService;
import com.example.otpjwt.service.JwtService;
import com.example.otpjwt.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final AuthService authService;

    @Value("${jwt.expiration.short}")
    private long shortExpiry;

    @Value("${jwt.expiration.long}")
    private long longExpiry;

    @PostMapping("/request-otp")
    public ResponseEntity<ApiResponse> requestOtp(@Valid @RequestBody RequestOtpRequest body,
                                                  HttpServletResponse response) {
        String phone = body.getPhoneNumber();
        if (!phone.matches("\\d{10}")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(400, "Phone number must be exactly 10 digits", null));
        }
        otpService.saveOtp(phone, shortExpiry / 1000);
        String jwtSessionToken = jwtService.generatePhoneToken(phone, shortExpiry);
        response.setHeader("Authorization", "Bearer " + jwtSessionToken);
        return ResponseEntity.ok(new ApiResponse(200, "OTP sent to your phone number", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest body,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        String phoneFromToken = (String) request.getAttribute("phoneNumber");
        if (phoneFromToken == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(400, "Missing or invalid session token", null));
        }

        if (!otpService.verifyOtp(phoneFromToken, body.getOtp())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(400, "Invalid or expired OTP", null));
        }

        User user = authService.getUserByPhone(phoneFromToken);
        String verificationToken = (user != null)
                ? jwtService.generateUserToken(user, longExpiry)
                : jwtService.generatePhoneToken(phoneFromToken, shortExpiry);

        response.setHeader("Authorization", "Bearer " + verificationToken);
        otpService.deleteOtp(phoneFromToken);
        return ResponseEntity.ok(new ApiResponse(200, "OTP verified successfully", user));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest body,
                                                  HttpServletResponse response,
                                                  HttpServletRequest request) {
        String phoneFromToken = (String) request.getAttribute("phoneNumber");
        if (phoneFromToken == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(400, "Missing or invalid session token", null));
        }

        if (!body.getPhoneNumber().equals(phoneFromToken)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(400, "Phone number does not match verified session", null));
        }

        try {
            User newUser = authService.signup(body.getFullName(), phoneFromToken);
            String token = jwtService.generateUserToken(newUser, longExpiry);
            response.setHeader("Authorization", "Bearer " + token);
            return ResponseEntity.status(201)
                    .body(new ApiResponse(201, "User created successfully", newUser));
        } catch (RuntimeException e) {
            // Catch the exception and return a 409 Conflict status
            return ResponseEntity.status(409)
                    .body(new ApiResponse(409, e.getMessage(), null));
        }
    }
}