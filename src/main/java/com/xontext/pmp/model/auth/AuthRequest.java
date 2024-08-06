package com.xontext.pmp.model.auth;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String username;
    private String email;
    private String password;
    private String authToken;
    private String accessToken;
    private String refreshToken;
}