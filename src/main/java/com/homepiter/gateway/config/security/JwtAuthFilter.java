package com.homepiter.gateway.security;

import com.homepiter.gateway.config.security.JwtTokenProvider;
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

    // 필터 로직 구현
    @Override
    public GatewayFilter apply(Config config) {
        return this::filter;
    }

    private Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Authorization 헤더 없으면 거절
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        token = token.replace("Bearer ", "");

        if (!jwtTokenProvider.validateToken(token)) {
            return onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
        }

        // 클레임 파싱
        Claims claims = jwtTokenProvider.parseToken(token);

        // 사용자 정보 헤더에 추가
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Email", claims.getSubject())
                .header("X-User-Role", claims.get("roles").toString())
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    // 오류 응답
    private Mono<Void> onError(ServerWebExchange exchange, String errMsg, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    // 설정용 내부 클래스
    public static class Config {}
}
