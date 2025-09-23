package com.gymcrm.service;

import com.gymcrm.dto.LoginRequest;
import com.gymcrm.security.CustomUserDetailsService;
import com.gymcrm.security.JwtService;
import com.gymcrm.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private AuthenticationService authenticationService;
    private CustomUserDetailsService userDetailsService;
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        loginAttemptService = mock(LoginAttemptService.class);
        authenticationService = new AuthenticationService(
                authenticationManager,
                jwtService,
                userDetailsService,
                loginAttemptService
        );
    }


    @Test
    void login_shouldReturnTokenWhenAuthenticationSucceeds() {
        LoginRequest request = new LoginRequest("nina", "pass123");
        String expectedToken = "mock-jwt-token";

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("nina");

        when(loginAttemptService.isBlocked("nina")).thenReturn(false);
        when(userDetailsService.loadUserByUsername("nina")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn(expectedToken);

        String actualToken = authenticationService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("nina", "pass123")
        );
        assertEquals(expectedToken, actualToken);
    }



    @Test
    void authenticate_shouldCallAuthenticationManager() {
        authenticationService.authenticate("user", "pwd");

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("user", "pwd")
        );
    }
}
