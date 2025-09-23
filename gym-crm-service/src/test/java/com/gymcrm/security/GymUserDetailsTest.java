package com.gymcrm.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GymUserDetailsTest {

    @Test
    void getters_shouldReturnCorrectValues() {
        GymUserDetails userDetails = new GymUserDetails("nina", "pass123", "ROLE_TRAINEE");

        assertEquals("nina", userDetails.getUsername());
        assertEquals("pass123", userDetails.getPassword());
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_TRAINEE")), userDetails.getAuthorities());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}
