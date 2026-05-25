package com.example.Auth_Service.Controller;

import com.example.Auth_Service.DTO.AuthResponse;
import com.example.Auth_Service.DTO.loginRequest;
import com.example.Auth_Service.DTO.registerRequest;
import com.example.Auth_Service.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public String register(
            @RequestBody registerRequest request
    ) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody loginRequest request
    ) {

        return authService.login(request);
    }
}
