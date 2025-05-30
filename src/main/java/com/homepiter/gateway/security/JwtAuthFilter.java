package com.homepiter.gateway.security;

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

@Component  // Spring Bean 으로 등록
@RequiredArgsConstructor  // final 필드인 jwtTokenProvider를 생성자 주입
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtTokenProvider jwtTokenProvider;

    // 필터를 적용할 때 호출되는 메서드
    @Override
    public GatewayFilter apply(Config config) {
        return this::filter;
    }

    // 인증 실패 시 에러 응답을 반환하는 메서드
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus); // 상태 코드 설정 (401, 403 등)
        return response.setComplete();      // 응답 종료
    }

    // 실제 필터 로직
    private Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Authorization 헤더가 없으면 401 반환
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Authorization 헤더가 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        // Bearer 토큰 파싱
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");

        // JWT 유효성 검사 실패 시 401 반환
        if (!jwtTokenProvider.validateToken(token)) {
            return onError(exchange, "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        // 토큰에서 사용자 정보 추출
        Claims claims = jwtTokenProvider.parseToken(token);

        // 기존 요청에 사용자 정보 헤더 추가 (X-User-Email, X-User-Role)
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Email", claims.getSubject()) // 이메일 (subject)
                .header("X-User-Role", claims.get("roles").toString()) // 역할 정보
                .build();

        // 다음 필터 체인으로 수정된 요청 전달
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    // 설정 클래스 (현재는 비어 있지만 args 받을 때 확장 가능)
    public static class Config {}
}
