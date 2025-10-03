package com.example.hospital.service;

import com.example.hospital.dto.response.AuthResponses;
import com.example.hospital.dto.request.LoginRequest;
import com.example.hospital.dto.request.RegisterRequest;


public interface AuthService {
    AuthResponses register(RegisterRequest request);
    AuthResponses login(LoginRequest request);
}
