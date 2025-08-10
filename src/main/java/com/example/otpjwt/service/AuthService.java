package com.example.otpjwt.service;

import com.example.otpjwt.model.User;
import com.example.otpjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

   public User signup(String fullName, String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isEmpty()) {
        throw new IllegalArgumentException("Phone number must not be empty or null");
    }
    if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
        throw new RuntimeException("User already exists with this phone number");
    }
    User u = new User();
    u.setFullName(fullName);
    u.setPhoneNumber(phoneNumber);
    return userRepository.save(u);
}


    public User getUserByPhone(String phone) {
        return userRepository.findByPhoneNumber(phone).orElse(null);
    }
}
