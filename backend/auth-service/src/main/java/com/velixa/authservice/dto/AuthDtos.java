package com.velixa.authservice.dto;

import com.velixa.authservice.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;

public final class AuthDtos {

    private AuthDtos() {}

    // ── Request DTOs ─────────────────────────────────────────────────────────

    public record RegisterRequest(
            @NotBlank(message = "First name is required")
            String firstName,

            @NotBlank(message = "Last name is required")
            String lastName,

            @NotBlank(message = "Username is required")
            @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
            String username,

            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}

    public record RefreshTokenRequest(
            @NotBlank(message = "Refresh token is required")
            String refreshToken
    ) {}

    public record ChangePasswordRequest(
            @NotBlank(message = "Current password is required")
            String currentPassword,

            @NotBlank(message = "New password is required")
            @Size(min = 8, message = "New password must be at least 8 characters")
            String newPassword
    ) {}

    public record UpdateProfileRequest(
            String firstName,
            String lastName,
            String avatarUrl
    ) {}

    // ── Response DTOs ────────────────────────────────────────────────────────

    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            UserResponse user
    ) {
        public static AuthResponse of(String accessToken, String refreshToken,
                                      long expiresIn, UserResponse user) {
            return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
        }
    }

    public record UserResponse(
            String id,
            String email,
            String username,
            String firstName,
            String lastName,
            String avatarUrl,
            Set<Role> roles,
            Instant createdAt,
            Instant lastLoginAt
    ) {}

    public record MessageResponse(
            String message,
            boolean success
    ) {
        public static MessageResponse ok(String message) {
            return new MessageResponse(message, true);
        }
        public static MessageResponse error(String message) {
            return new MessageResponse(message, false);
        }
    }

    public record TokenValidationResponse(
            boolean valid,
            String userId,
            String username,
            String email,
            Set<Role> roles
    ) {}
}
