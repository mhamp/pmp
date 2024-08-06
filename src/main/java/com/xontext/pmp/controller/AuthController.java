package com.xontext.pmp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xontext.pmp.model.Profile;
import com.xontext.pmp.model.User;
import com.xontext.pmp.model.auth.*;
import com.xontext.pmp.repository.TokenRepository;
import com.xontext.pmp.repository.UserRepository;
import com.xontext.pmp.service.UserService;
import com.xontext.pmp.service.auth.AuthService;
import com.xontext.pmp.service.auth.JwtService;
import com.xontext.pmp.service.core.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth/")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final UserService userService;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    @PostMapping("/register/user")
    public ResponseEntity registerUser(@Validated @RequestBody User user){
        try{
            if(userService.getUserByUsername(user.getUsername()).isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists. Try again with another name.");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User save = userService.createUser(user);
            Profile profile = Profile.builder()
                    .user(user)
                    .profileName(user.getUsername())
                    .build();
            profile = profileService.createProfile(user.getId(), profile);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * https://www.youtube.com/watch?v=NzuC52eLGKo
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
            AuthResponse response = authService.register(request);
            if(request.isMfaEnabled()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.accepted().build();
    }

    /**
     * https://github.com/ali-bouali/spring-boot-3-jwt-security/blob/main/src/main/java/com/alibou/security/auth/AuthenticationService.java
     * @param request
     * @return
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            User user = userService.getUserByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            Long userId = user.getId();

            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .userId(userId)
                    .build());
        } catch (AuthenticationException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(@Validated  @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = JwtService.generateToken(userDetails);
            Long userId = ((User) userDetails).getId();

            AuthResponse authResponse = AuthResponse.builder()
                    .jwt(jwt)
                    .accessToken(jwt) // or another token if you have a separate access token
                    .userId(userId)
                    .build();

            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationRequest verificationRequest){
        return ResponseEntity.ok(authService.verifyCode(verificationRequest));
    }
}
