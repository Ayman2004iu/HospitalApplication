package com.example.hospital.dto.request;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
}

