package com.velixa.authservice.service;

import com.velixa.authservice.dto.AuthDtos.*;
import com.velixa.authservice.exception.VelixaException;
import com.velixa.authservice.model.RefreshToken;
import com.velixa.authservice.model.User;
import com.velixa.authservice.repository.RefreshTokenRepository;
import com.velixa.authservice.repository.UserRepository;
import com.velixa.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw VelixaException.conflict("Email already registered");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw VelixaException.conflict("Username already taken");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        user = userRepository.save(user);
        log.info("Registered new user: {}", user.getEmail());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> VelixaException.notFound("User not found"));

        // Update last login
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshTokens(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> VelixaException.unauthorized("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            refreshTokenRepository.delete(refreshToken);
            throw VelixaException.unauthorized("Refresh token expired or revoked");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> VelixaException.notFound("User not found"));

        // Rotate refresh token
        refreshTokenRepository.delete(refreshToken);

        return generateAuthResponse(user);
    }

    @Transactional
    public MessageResponse logout(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("User {} logged out", userId);
        return MessageResponse.ok("Logged out successfully");
    }

    public TokenValidationResponse validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            return new TokenValidationResponse(false, null, null, null, null);
        }
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if (user == null) {
            return new TokenValidationResponse(false, null, null, null, null);
        }
        return new TokenValidationResponse(true, user.getId(),
                user.getUsername(), user.getEmail(), user.getRoles());
    }

    @Transactional
    public MessageResponse changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> VelixaException.notFound("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw VelixaException.badRequest("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Revoke all refresh tokens
        refreshTokenRepository.deleteByUserId(userId);

        return MessageResponse.ok("Password changed successfully");
    }

    public UserResponse getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> VelixaException.notFound("User not found"));
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> VelixaException.notFound("User not found"));

        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.avatarUrl() != null) user.setAvatarUrl(request.avatarUrl());

        return toUserResponse(userRepository.save(user));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user.getId());
        return AuthResponse.of(accessToken, refreshToken.getToken(),
                jwtUtil.getExpiration(), toUserResponse(user));
    }

    private RefreshToken createRefreshToken(String userId) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expiresAt(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        return refreshTokenRepository.save(token);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(),
                user.getFirstName(), user.getLastName(), user.getAvatarUrl(),
                user.getRoles(), user.getCreatedAt(), user.getLastLoginAt());
    }
}
