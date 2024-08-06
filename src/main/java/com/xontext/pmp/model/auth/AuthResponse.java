package com.xontext.pmp.model.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthResponse {
    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }

    private String jwt;
    private String accessToken;
    private String refreshToken;
    private Boolean mfaEnabled;
    private String secretImageUri;
    private Long userId;
}