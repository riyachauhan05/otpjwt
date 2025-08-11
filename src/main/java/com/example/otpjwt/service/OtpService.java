
package com.example.otpjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;


@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private static final String HARDCODED_OTP = "123456";

    private String buildOtpKey(String phoneNumber) {
        return "otp:phone:" + phoneNumber;
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing OTP", e);
        }
    }

    public void saveOtp(String phoneNumber, long expirySeconds) {
        String hashedOtp = hashOtp(HARDCODED_OTP);
        redisTemplate.opsForValue().set(buildOtpKey(phoneNumber), hashedOtp, Duration.ofSeconds(expirySeconds));
    }

    public boolean verifyOtp(String phoneNumber, String providedOtp) {
        String storedHashedOtp = redisTemplate.opsForValue().get(buildOtpKey(phoneNumber));
        if (storedHashedOtp == null) return false;
        String hashedProvidedOtp = hashOtp(providedOtp);
        return storedHashedOtp.equals(hashedProvidedOtp);
    }

    public void deleteOtp(String phoneNumber) {
        redisTemplate.delete(buildOtpKey(phoneNumber));
    }
}