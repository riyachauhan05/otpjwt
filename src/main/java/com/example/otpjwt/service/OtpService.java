// package com.example.otpjwt.service;

// import org.springframework.stereotype.Service;

// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// @Service
// public class OtpService {

//     private final Map<String, String> otpStore = new ConcurrentHashMap<>();
//     private final Map<String, Long> expiryStore = new ConcurrentHashMap<>();

//     public void saveOtp(String phoneNumber, String otp, long expirySeconds) {
//         otpStore.put(phoneNumber, otp);
//         expiryStore.put(phoneNumber, System.currentTimeMillis() + expirySeconds * 1000);
//     }

//     public String getOtp(String phoneNumber) {
//         Long expiry = expiryStore.get(phoneNumber);
//         if (expiry == null || System.currentTimeMillis() > expiry) {
//             otpStore.remove(phoneNumber);
//             expiryStore.remove(phoneNumber);
//             return null;
//         }
//         return otpStore.get(phoneNumber);
//     }

//     public void deleteOtp(String phoneNumber) {
//         otpStore.remove(phoneNumber);
//         expiryStore.remove(phoneNumber);
//     }
// }


package com.example.otpjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    // Hardcoded OTP for demonstration purposes
    private static final String HARDCODED_OTP = "123456";

    public void saveOtp(String phoneNumber, long expirySeconds) {
        // Store the hardcoded OTP directly in Redis
        redisTemplate.opsForValue().set("otp:" + phoneNumber, HARDCODED_OTP, Duration.ofSeconds(expirySeconds));
    }

    public boolean verifyOtp(String phoneNumber, String providedOtp) {
        String storedOtp = redisTemplate.opsForValue().get("otp:" + phoneNumber);
        if (storedOtp == null) {
            return false; // OTP not found or expired
        }
        return storedOtp.equals(providedOtp);
    }

    public void deleteOtp(String phoneNumber) {
        redisTemplate.delete("otp:" + phoneNumber);
    }
}