// package com.example.otpjwt.service;

// import com.example.otpjwt.model.User;
// import io.jsonwebtoken.*;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.stereotype.Service;

// import java.security.Key;
// import java.util.Date;
// import java.util.Map;
// import java.util.function.Function;

// @Service
// public class JwtService {

//     // A 256-bit key for HS256. Replace with a secure, stored key in real apps.
//     private static final String SECRET_KEY = "mysecretkeymysecretkeymysecretkey123"; 

//     private Key getSigningKey() {
//         return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//     }

//     /**
//      * Generate a token with custom claims
//      */
//     public String generateToken(Map<String, Object> extraClaims, String subject, long expirySeconds) {
//         return Jwts.builder()
//                 .setClaims(extraClaims)
//                 .setSubject(subject)
//                 .setIssuedAt(new Date(System.currentTimeMillis()))
//                 .setExpiration(new Date(System.currentTimeMillis() + expirySeconds * 1000))
//                 .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                 .compact();
//     }

//     /**
//      * Short-lived phone token
//      */
//     public String generatePhoneToken(String phoneNumber, long expirySeconds) {
//         return generateToken(Map.of("phoneNumber", phoneNumber), phoneNumber, expirySeconds);
//     }

//     /**
//      * Long-lived user token with username + phone number
//      */
//     public String generateUserToken(User user, long expirySeconds) {
//         return generateToken(
//                 Map.of(
//                         "fullName", user.getFullName(),
//                         "phoneNumber", user.getPhoneNumber()
//                 ),
//                 user.getPhoneNumber(),
//                 expirySeconds
//         );
//     }

//     /**
//      * Extract any claim
//      */
//     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//         final Claims claims = extractAllClaims(token);
//         return claimsResolver.apply(claims);
//     }

//     /**
//      * Extract subject (phone number here)
//      */
//     public String extractUsername(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     /**
//      * Validate token
//      */
//     public boolean validateToken(String token) {
//         try {
//             extractAllClaims(token); // will throw if invalid
//             return !isTokenExpired(token);
//         } catch (JwtException | IllegalArgumentException e) {
//             return false;
//         }
//     }

//     private boolean isTokenExpired(String token) {
//         return extractExpiration(token).before(new Date());
//     }

//     private Date extractExpiration(String token) {
//         return extractClaim(token, Claims::getExpiration);
//     }

//     private Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(getSigningKey())
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }
// }


package com.example.otpjwt.service;

import com.example.otpjwt.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateUserToken(User user, long expiry) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getPhoneNumber())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generatePhoneToken(String phoneNumber, long expiry) {
        return Jwts.builder()
                .setSubject(phoneNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}