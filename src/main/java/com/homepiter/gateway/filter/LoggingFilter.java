package com.homepiter.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Instant startTime = Instant.now();

            ServerHttpRequest request = exchange.getRequest();
            String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
            String path = request.getURI().getRawPath();
            String requestId = UUID.randomUUID().toString();

            log.info("요청  | [{}] {} | 요청 ID: {}", method, path, requestId);

            return chain.filter(exchange)
                    .doOnSuccess(aVoid -> {
                        ServerHttpResponse response = exchange.getResponse();
                        int statusCode = response.getRawStatusCode() != null ? response.getRawStatusCode() : 0;
                        Duration duration = Duration.between(startTime, Instant.now());

                        log.info("완료 | [{}] {} | 상태: {} | 처리 시간: {}ms | 요청 ID: {}",
                                method, path, statusCode, duration.toMillis(), requestId);
                    });
        };
    }

    public static class Config {
        // application.yml에서 설정을 받을 때 사용될 수 있는 클래스
    }
}
