package com.velixa.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velixa.authservice.dto.AuthDtos.*;
import com.velixa.authservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void register_withValidData_returns201() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "John", "Doe", "johndoe",
                "john@example.com", "Password123!");

        AuthResponse response = AuthResponse.of(
                "access-token", "refresh-token", 86400000L,
                new UserResponse("1", "john@example.com", "johndoe",
                        "John", "Doe", null, Set.of(), Instant.now(), null));

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));
    }

    @Test
    void register_withInvalidEmail_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "John", "Doe", "johndoe",
                "not-an-email", "Password123!");

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withValidCredentials_returns200() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "Password123!");

        AuthResponse response = AuthResponse.of(
                "access-token", "refresh-token", 86400000L,
                new UserResponse("1", "john@example.com", "johndoe",
                        "John", "Doe", null, Set.of(), Instant.now(), null));

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @WithMockUser
    void validate_withValidHeader_returns200() throws Exception {
        TokenValidationResponse tvr = new TokenValidationResponse(
                true, "1", "johndoe", "john@example.com", Set.of());

        when(authService.validateToken(any())).thenReturn(tvr);

        mockMvc.perform(get("/api/v1/auth/validate")
                        .header("Authorization", "Bearer some-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }
}
