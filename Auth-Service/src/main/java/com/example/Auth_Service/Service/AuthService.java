package com.example.Auth_Service.Service;

import com.example.Auth_Service.DTO.AuthResponse;
import com.example.Auth_Service.DTO.LogMessage;
import com.example.Auth_Service.DTO.loginRequest;
import com.example.Auth_Service.DTO.registerRequest;
import com.example.Auth_Service.Entity.User;
import com.example.Auth_Service.Kafka.LogProducer;
import com.example.Auth_Service.Repository.userRepo;
import com.example.Auth_Service.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final userRepo  userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LogProducer logProducer;

    public String register(registerRequest request){
        User user= User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepo.save(user);
        logProducer.sendlog(
                new LogMessage(
                        "AUTH-SERVICE",
                        "INFO",
                        "User Registered Successful",
                        LocalDateTime.now()
                )
        );
        return "User Registered successfully!";
    }

    public AuthResponse login(loginRequest request){
        User user = userRepo.findByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new RuntimeException("invalid password");
        }
        String token = jwtUtil.genrateToken(user.getEmail());
        logProducer.sendlog(
                new LogMessage(
                        "AUTH-SERVICE",
                        "INFO",
                        "User Login Successful",
                        LocalDateTime.now()
                )
        );

        return new AuthResponse(token);
    }
}
