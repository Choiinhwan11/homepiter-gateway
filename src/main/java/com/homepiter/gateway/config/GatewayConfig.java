package com.homepiter.gateway.config;

import com.homepiter.gateway.security.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public GatewayConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                //  유저 서비스 라우팅 (JWT 필터 적용)
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://USER-SERVICE"))

                //  비즈니스 서비스 라우팅 (JWT 필터 적용)
                .route("business-service", r -> r.path("/api/business/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://BUSINESS-SERVICE"))

                //  관리자 서비스 라우팅 (JWT 필터 적용)
                .route("admin-service", r -> r.path("/api/admin/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://ADMIN-SERVICE"))
                // chat routing
                .route("chat-service", r -> r.path("/api/chat/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://CHAT-SERVICE"))
                //coommons
                .route("coommons-service", r -> r.path("/api/coommons/**")
                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
                        .uri("lb://COOMMONS-SERVICE"))

                .build();
    }
}
