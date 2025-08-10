package com.example.otpjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.arangodb.springframework.annotation.EnableArangoRepositories;

@SpringBootApplication
@EnableArangoRepositories(basePackages = "com.example.otpjwt.repository")
public class OtpJwtArangodbApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtpJwtArangodbApplication.class, args);
    }
}
