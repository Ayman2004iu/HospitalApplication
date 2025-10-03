package com.example.hospital.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponses {
    private String token;
    private String username;
    private String email;
}

