package com.example.otpjwt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public: can request OTP without any token
                .requestMatchers("/api/auth/request-otp", "/error").permitAll()

                // Verify OTP: still public for Spring Security, OTP checked in controller/service
                .requestMatchers("/api/auth/verify-otp").permitAll()

                // Signup: must have a valid short-lived JWT (from verify-otp)
                .requestMatchers("/api/auth/signup").authenticated()

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            // No HTTP session, rely solely on JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Add JWT filter before username/password auth filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}