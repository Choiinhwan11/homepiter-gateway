package com.homepiter.gateway.config.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public GatewayFilter apply(Config config) {
        return this::filter;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");

        if (!jwtTokenProvider.validateToken(token)) {
            return onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
        }

        Claims claims = jwtTokenProvider.parseToken(token);
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Email", claims.getSubject())
                .header("X-User-Role", claims.get("roles").toString()) // 역할(Role) 전달
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    public static class Config {}
}
