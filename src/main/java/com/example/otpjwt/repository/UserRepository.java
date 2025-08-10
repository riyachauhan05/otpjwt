package com.example.otpjwt.repository;

import com.example.otpjwt.model.User;
import com.arangodb.springframework.repository.ArangoRepository;

import java.util.Optional;

public interface UserRepository extends ArangoRepository<User, String> {
    Optional<User> findByPhoneNumber(String phoneNumber);
}
