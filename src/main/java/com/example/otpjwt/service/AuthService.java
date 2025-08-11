// package com.example.otpjwt.service;

// import com.example.otpjwt.model.User;
// import com.example.otpjwt.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// @Service
// @RequiredArgsConstructor
// public class AuthService {

//     private final UserRepository userRepository;

//     public User signup(String fullName, String phoneNumber) {
//         // Validate phone format
//         if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
//             throw new IllegalArgumentException("Phone number must be exactly 10 digits");
//         }

//         // Check for duplicate
//         if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
//             throw new RuntimeException("User already exists with this phone number");
//         }

//         // Save new user
//         User u = new User();
//         u.setFullName(fullName);
//         u.setPhoneNumber(phoneNumber);
//         return userRepository.save(u);
//     }

//     public User getUserByPhone(String phoneNumber) {
//         return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
//     }
// }

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
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
        }

        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new RuntimeException("User already exists with this phone number");
        }

        User u = new User();
        u.setFullName(fullName);
        u.setPhoneNumber(phoneNumber);
        u.setVerified(true);
        return userRepository.save(u);
    }

    public User getUserByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }
}