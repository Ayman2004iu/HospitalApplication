package com.example.hospital.controller;

import com.example.hospital.dto.response.AuthResponses;
import com.example.hospital.dto.request.LoginRequest;
import com.example.hospital.dto.request.RegisterRequest;
import com.example.hospital.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;


    public AuthController(AuthService authService) {

        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponses> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponses> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

