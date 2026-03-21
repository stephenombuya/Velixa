package com.velixa.authservice.service;

import com.velixa.authservice.dto.AuthDtos.*;
import com.velixa.authservice.exception.VelixaException;
import com.velixa.authservice.model.RefreshToken;
import com.velixa.authservice.model.Role;
import com.velixa.authservice.model.User;
import com.velixa.authservice.repository.RefreshTokenRepository;
import com.velixa.authservice.repository.UserRepository;
import com.velixa.authservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-1")
                .email("john@example.com")
                .username("johndoe")
                .firstName("John")
                .lastName("Doe")
                .password("encoded-password")
                .roles(Set.of(Role.ROLE_USER))
                .enabled(true)
                .build();
    }

    @Test
    void register_newUser_returnsAuthResponse() {
        RegisterRequest req = new RegisterRequest("John", "Doe", "johndoe",
                "john@example.com", "Password123!");

        when(userRepository.existsByEmail(req.email())).thenReturn(false);
        when(userRepository.existsByUsername(req.username())).thenReturn(false);
        when(passwordEncoder.encode(req.password())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(testUser);
        when(jwtUtil.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);
        when(refreshTokenRepository.save(any())).thenReturn(
                RefreshToken.builder().token("refresh-token").userId("user-1")
                        .expiresAt(Instant.now().plusSeconds(3600)).build());

        AuthResponse response = authService.register(req);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.user().email()).isEqualTo("john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest req = new RegisterRequest("John", "Doe", "johndoe",
                "john@example.com", "Password123!");

        when(userRepository.existsByEmail(req.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(VelixaException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void register_duplicateUsername_throwsConflict() {
        RegisterRequest req = new RegisterRequest("John", "Doe", "johndoe",
                "john@example.com", "Password123!");

        when(userRepository.existsByEmail(req.email())).thenReturn(false);
        when(userRepository.existsByUsername(req.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(VelixaException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    void login_validCredentials_returnsAuthResponse() {
        LoginRequest req = new LoginRequest("john@example.com", "Password123!");

        when(userRepository.findByEmail(req.email())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtUtil.getExpiration()).thenReturn(86400000L);
        when(userRepository.save(any())).thenReturn(testUser);
        when(refreshTokenRepository.save(any())).thenReturn(
                RefreshToken.builder().token("refresh-token").userId("user-1")
                        .expiresAt(Instant.now().plusSeconds(3600)).build());

        AuthResponse response = authService.login(req);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void logout_revokesRefreshTokens() {
        MessageResponse response = authService.logout("user-1");

        verify(refreshTokenRepository).deleteByUserId("user-1");
        assertThat(response.success()).isTrue();
    }

    @Test
    void validateToken_validToken_returnsValidResponse() {
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.extractEmail("valid-token")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        TokenValidationResponse response = authService.validateToken("valid-token");

        assertThat(response.valid()).isTrue();
        assertThat(response.email()).isEqualTo("john@example.com");
    }

    @Test
    void validateToken_invalidToken_returnsInvalidResponse() {
        when(jwtUtil.validateToken("bad-token")).thenReturn(false);

        TokenValidationResponse response = authService.validateToken("bad-token");

        assertThat(response.valid()).isFalse();
    }
}
