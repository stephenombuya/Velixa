package com.velixa.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class GatewayConfig {

    /**
     * Rate-limit key by authenticated user ID header (set by AuthenticationFilter).
     * Falls back to IP when the header is missing (public routes).
     */
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                return Mono.just(userId);
            }
            // fallback to IP
            return Mono.just(
                    Objects.requireNonNullElse(
                            exchange.getRequest().getRemoteAddress(),
                            exchange.getRequest().getLocalAddress()
                    ).toString()
            );
        };
    }

    /** Rate-limit key by client IP address (used for public/auth endpoints). */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNullElse(
                        exchange.getRequest().getRemoteAddress(),
                        exchange.getRequest().getLocalAddress()
                ).getAddress().getHostAddress()
        );
    }
}
