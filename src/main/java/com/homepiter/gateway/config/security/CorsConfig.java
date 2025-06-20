package com.homepiter.gateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        //  모든 오리진(출처) 허용
        config.setAllowedOriginPatterns(List.of("*"));

        //  HTTP 메서드 허용
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        //  모든 헤더 허용
        config.setAllowedHeaders(Arrays.asList("*"));

        //  응답 헤더 허용 (Authorization 헤더 포함)
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        //  인증 정보를 포함한 요청 허용 (ex: 쿠키 사용 가능)
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
