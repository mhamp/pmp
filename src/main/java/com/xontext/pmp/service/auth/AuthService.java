package com.xontext.pmp.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xontext.pmp.model.Profile;
import com.xontext.pmp.model.ProfileAttribute;
import com.xontext.pmp.model.User;
import com.xontext.pmp.model.auth.*;
import com.xontext.pmp.repository.ProfileAttributeRepository;
import com.xontext.pmp.repository.ProfileRepository;
import com.xontext.pmp.repository.TokenRepository;
import com.xontext.pmp.repository.UserRepository;
import com.xontext.pmp.service.core.AttributeService;
import com.xontext.pmp.util.UserAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final ProfileRepository profileRepository;
    private final AttributeService attributeService;
    private final ProfileAttributeRepository profileAttributeRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwoFactorAuthService twoFactorAuthService;
    private final UserRepository userRepository;
    public AuthResponse register(RegisterRequest request){

        if (repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .mfaEnabled(request.isMfaEnabled())
                .build();

        Profile profile = Profile.builder()
                .profileName(request.getFirstname() + " " + request.getLastname())
                .user(user)
                .build();

        if (request.isMfaEnabled()){
            user.setSecret(twoFactorAuthService.generateNewSecret());
        }
        // Save the user and handle potential errors



        User savedUser;
        Profile savedProfile;
        try {
            savedUser = repository.save(user);
            savedProfile = profileRepository.save(profile);
            savePersonalInfo(savedUser, savedProfile);
        } catch (Exception e) {
            // Handle exception, log error and rethrow or return appropriate response
            throw new RuntimeException("Error saving user", e);
        }

        var jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthResponse.builder()
                .mfaEnabled(user.isMfaEnabled())
                .secretImageUri(twoFactorAuthService.generateQrCodeImageUri(user.getSecret()))
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void savePersonalInfo(User user, Profile profile) {
        ProfileAttribute profileAttributeName = ProfileAttribute.builder()
                .stringValue(user.getFirstname() + " " + user.getLastname())
                .profile(profile)
                .attribute(attributeService.getAttributeById(1L))
                .build();
        try{
            profileAttributeRepository.save(profileAttributeName);
        }catch (Exception e) {
            // Handle exception, log error and rethrow or return appropriate response
            throw new RuntimeException("Error saving user", e);
        }
    }


    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        if (user.isMfaEnabled()){
            return AuthResponse.builder()
                    .accessToken("")
                    .refreshToken("")
                    .mfaEnabled(true)
                    .build();
        }
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .mfaEnabled(false)
                .build();
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
            var user = repository.findByEmail(userEmail)
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

    public AuthResponse verifyCode(VerificationRequest verificationRequest) {
        User user  = userRepository
                .findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("No user found with %S", verificationRequest.getEmail())
                ));
        if(twoFactorAuthService.isOtpNotValid(user.getSecret(), verificationRequest.getCode())){
            throw new BadCredentialsException("Code is not correct.");
        }
        String jwt = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(jwt)
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }
}
