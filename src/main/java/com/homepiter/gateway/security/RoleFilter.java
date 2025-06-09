package com.homepiter.gateway.config.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RoleFilter extends AbstractGatewayFilterFactory<RoleFilter.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey("X-User-Role")) {
                return onError(exchange, "Missing User Role", HttpStatus.FORBIDDEN);
            }

            String userRole = request.getHeaders().getFirst("X-User-Role");

            if (!userRole.equals(config.requiredRole)) {
                return onError(exchange, "Unauthorized Role", HttpStatus.FORBIDDEN);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        private String requiredRole;
        public void setRequiredRole(String requiredRole) {
            this.requiredRole = requiredRole;
        }
    }
}
