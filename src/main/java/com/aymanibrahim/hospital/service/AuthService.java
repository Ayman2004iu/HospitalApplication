package com.aymanibrahim.hospital.service;

import com.aymanibrahim.hospital.dto.response.AuthResponses;
import com.aymanibrahim.hospital.dto.request.LoginRequest;
import com.aymanibrahim.hospital.dto.request.RegisterRequest;


public interface AuthService {
    AuthResponses register(RegisterRequest request);
    AuthResponses login(LoginRequest request);
}
