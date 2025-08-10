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

        // Hardcoded OTP for demo
        String otp = "123456";
        otpService.saveOtp(phone, otp, shortExpiry);

        String jwtSessionToken = jwtService.generateToken(phone, shortExpiry);
        response.setHeader("Authorization", "Bearer " + jwtSessionToken);

        return ResponseEntity.ok(new ApiResponse(200, "OTP sent to your phone number", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest body,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        String phoneFromToken = (String) request.getAttribute("phoneNumber");
        if (phoneFromToken == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, "Missing or invalid session token", null));
        }

        String storedOtp = otpService.getOtp(phoneFromToken);
        if (storedOtp == null || !storedOtp.equals(body.getOtp())) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, "Invalid or expired OTP", null));
        }

        User user = authService.getUserByPhone(phoneFromToken);

        long expiry = (user != null) ? longExpiry : shortExpiry;
        String verificationToken = jwtService.generateToken(phoneFromToken, expiry);
        response.setHeader("Authorization", "Bearer " + verificationToken);

        otpService.deleteOtp(phoneFromToken);

        return ResponseEntity.ok(new ApiResponse(200, "OTP verified successfully", user));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest body,
                                              HttpServletResponse response) {
        User newUser = authService.signup(body.getFullName(), body.getPhoneNumber());
        String token = jwtService.generateToken(newUser.getPhoneNumber(), longExpiry);
        response.setHeader("Authorization", "Bearer " + token);
        return ResponseEntity.status(201).body(new ApiResponse(201, "User created successfully", newUser));
    }
}
