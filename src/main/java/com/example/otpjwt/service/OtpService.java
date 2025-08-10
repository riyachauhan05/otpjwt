package com.example.otpjwt.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Map<String, Long> expiryStore = new ConcurrentHashMap<>();

    public void saveOtp(String phoneNumber, String otp, long expirySeconds) {
        otpStore.put(phoneNumber, otp);
        expiryStore.put(phoneNumber, System.currentTimeMillis() + expirySeconds * 1000);
    }

    public String getOtp(String phoneNumber) {
        Long expiry = expiryStore.get(phoneNumber);
        if (expiry == null || System.currentTimeMillis() > expiry) {
            otpStore.remove(phoneNumber);
            expiryStore.remove(phoneNumber);
            return null;
        }
        return otpStore.get(phoneNumber);
    }

    public void deleteOtp(String phoneNumber) {
        otpStore.remove(phoneNumber);
        expiryStore.remove(phoneNumber);
    }
}
