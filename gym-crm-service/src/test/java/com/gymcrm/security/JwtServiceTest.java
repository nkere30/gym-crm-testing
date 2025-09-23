package com.gymcrm.security;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "verylongsupersecretkeyforjwtgeneration1234567890";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", secret);
        jwtService.init();
    }

    @Test
    void generateAndExtract_shouldReturnCorrectUsernameAndRole() {
        var user = new User("nina", "pwd", Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINEE")));
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals("nina", jwtService.extractUsername(token));
        assertEquals("ROLE_TRAINEE", jwtService.extractRole(token));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        var user = new User("nina", "pwd", Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINEE")));
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, "nina"));
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidUsername() {
        var user = new User("nina", "pwd", Collections.singletonList(new SimpleGrantedAuthority("ROLE_TRAINEE")));
        String token = jwtService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token, "otherUser"));
    }

    @Test
    void isTokenValid_shouldReturnFalseForMalformedToken() {
        String malformedToken = "not.a.valid.token";
        assertFalse(jwtService.isTokenValid(malformedToken, "nina"));
    }
}
