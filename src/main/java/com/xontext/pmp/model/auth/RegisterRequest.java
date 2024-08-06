package com.xontext.pmp.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
    private boolean mfaEnabled;
    private String secret;
}
