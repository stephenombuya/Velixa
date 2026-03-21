package com.velixa.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Gateway-level JWT validation filter.
 *
 * When a valid token is found it enriches the downstream request with:
 *   X-User-Id      → JWT claim "userId"
 *   X-Username     → JWT claim "username"
 *   X-User-Email   → JWT subject (email)
 *   X-User-Roles   → comma-separated JWT claim "roles"
 *
 * Downstream services trust these headers rather than re-parsing the token.
 */
@Component
@Slf4j
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or malformed Authorization header",
                        HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = parseClaims(token);

                String userId   = claims.get("userId", String.class);
                String username = claims.get("username", String.class);
                String email    = claims.getSubject();
                List<?> roles   = claims.get("roles", List.class);
                String rolesStr = roles == null ? "" : String.join(",",
                        roles.stream().map(Object::toString).toList());

                // Strip incoming user-identity headers (prevent spoofing)
                ServerHttpRequest mutated = request.mutate()
                        .headers(h -> {
                            h.remove("X-User-Id");
                            h.remove("X-Username");
                            h.remove("X-User-Email");
                            h.remove("X-User-Roles");
                        })
                        .header("X-User-Id",    userId   != null ? userId   : "")
                        .header("X-Username",   username != null ? username : "")
                        .header("X-User-Email", email    != null ? email    : "")
                        .header("X-User-Roles", rolesStr)
                        .build();

                return chain.filter(exchange.mutate().request(mutated).build());

            } catch (JwtException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format(
                "{\"errorCode\":\"%s\",\"message\":\"%s\",\"status\":%d}",
                status.name(), message, status.value());
        var buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {}
}
