package com.example.payment_service.security;

import com.example.payment_service.DTO.LogMessage;
import com.example.payment_service.kafka.LogProducer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final LogProducer logProducer;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.toLowerCase().startsWith("bearer ")) {

            logger.warn("Authorization header missing or not Bearer; continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authHeader.substring(authHeader.indexOf(' ') + 1).trim();

        if (!jwtUtil.validateToken(token)) {
            logger.warn("JWT validation failed for request to {}", request.getRequestURI());
            logProducer.sendlog(
                    new LogMessage(
                            "PAYMENT-SERVICE",
                            "WARN",
                            "Invalid JWT Token",
                            LocalDateTime.now()
                    )
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        String username =
                jwtUtil.extractUsername(token);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(
                request,
                response
        );
    }
}