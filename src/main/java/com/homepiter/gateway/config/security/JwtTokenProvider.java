package com.homepiter.gateway.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

@Component
public class JwtTokenProvider {

    private static final Logger logger = Logger.getLogger(JwtTokenProvider.class.getName());

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    private Key secretKey;

    @jakarta.annotation.PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            logger.warning("Invalid JWT: " + e.getMessage());
        }
        return false;
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
