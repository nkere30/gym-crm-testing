package com.gymcrm.workload.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, "12345678901234567890123456789012");

        jwtService.init();
    }

    @Test
    void parse_shouldReturnClaims() throws Exception {
        String token = Jwts.builder()
                .setSubject("gym-crm-service")
                .claim("role", "ROLE_SERVICE")
                .setIssuedAt(new Date())
                .signWith(getKeyFromService(), SignatureAlgorithm.HS256)
                .compact();

        Claims claims = jwtService.parse(token).getBody();

        assertThat(claims.getSubject()).isEqualTo("gym-crm-service");
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_SERVICE");
    }

    // Helper to access private "key" field
    private java.security.Key getKeyFromService() throws Exception {
        Field keyField = JwtService.class.getDeclaredField("key");
        keyField.setAccessible(true);
        return (java.security.Key) keyField.get(jwtService);
    }
}
