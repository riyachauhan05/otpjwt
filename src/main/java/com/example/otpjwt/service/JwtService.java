package com.example.otpjwt.service;

import com.example.otpjwt.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // A 256-bit key for HS256. Replace with a secure, stored key in real apps.
    private static final String SECRET_KEY = "mysecretkeymysecretkeymysecretkey123"; 

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Generate a token with custom claims
     */
    public String generateToken(Map<String, Object> extraClaims, String subject, long expirySeconds) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirySeconds * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Short-lived phone token
     */
    public String generatePhoneToken(String phoneNumber, long expirySeconds) {
        return generateToken(Map.of("phoneNumber", phoneNumber), phoneNumber, expirySeconds);
    }

    /**
     * Long-lived user token with username + phone number
     */
    public String generateUserToken(User user, long expirySeconds) {
        return generateToken(
                Map.of(
                        "fullName", user.getFullName(),
                        "phoneNumber", user.getPhoneNumber()
                ),
                user.getPhoneNumber(),
                expirySeconds
        );
    }

    /**
     * Extract any claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract subject (phone number here)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // will throw if invalid
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
