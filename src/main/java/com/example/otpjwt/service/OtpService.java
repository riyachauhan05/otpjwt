package com.example.otpjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;

    public void saveOtp(String phone, String otp, long expiryMillis) {
        redisTemplate.opsForValue().set(phone, otp, expiryMillis, TimeUnit.MILLISECONDS);
    }

    public String getOtp(String phone) {
        return redisTemplate.opsForValue().get(phone);
    }

    public void deleteOtp(String phone) {
        redisTemplate.delete(phone);
    }
}
