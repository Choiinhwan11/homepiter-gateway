//package com.homepiter.gateway.config.gatway;
//
//import com.homepiter.gateway.config.security.JwtAuthFilter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfig {
//
//    private final JwtAuthFilter jwtAuthFilter;
//
//    public GatewayConfig(JwtAuthFilter jwtAuthFilter) {
//        this.jwtAuthFilter = jwtAuthFilter;
//    }
//
//    @Bean
//    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("user-service", r -> r.path("/api/users/**")
//                        .filters(f -> f.filter(jwtAuthFilter.apply(new JwtAuthFilter.Config())))
//                        .uri("lb://USER-SERVICE"))
//                .build();
//    }
//}
