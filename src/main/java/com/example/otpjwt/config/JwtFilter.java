package com.example.otpjwt.config;

import com.example.otpjwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtService.isTokenValid(token)) {
                    String phoneNumber = jwtService.extractSubject(token);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    phoneNumber,
                                    null,
                                    Collections.emptyList()
                            );

                    // ✅ Set authentication for current request
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // ✅ Pass phoneNumber to controller (useful for fetching user data)
                    request.setAttribute("phoneNumber", phoneNumber);

                } else {
                    log.warn("❌ JWT validation failed - Invalid or expired token");
                }
            } catch (Exception e) {
                log.error("❌ JWT processing error: {}", e.getMessage());
            }
        } else {
            log.debug("No Authorization header or invalid format");
        }

        filterChain.doFilter(request, response);
    }
}