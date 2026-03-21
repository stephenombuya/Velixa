package com.velixa.authservice.controller;

import com.velixa.authservice.dto.AuthDtos.*;
import com.velixa.authservice.model.User;
import com.velixa.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth operations: register, login, refresh, logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return tokens")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshTokens(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh tokens", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MessageResponse> logout(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.logout(user.getId()));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate an access token (used by API Gateway)")
    public ResponseEntity<TokenValidationResponse> validate(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getProfile(user.getId()));
    }

    @PatchMapping("/me")
    @Operation(summary = "Update current user profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(user.getId(), request));
    }

    @PostMapping("/me/change-password")
    @Operation(summary = "Change password", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(user.getId(), request));
    }
}
