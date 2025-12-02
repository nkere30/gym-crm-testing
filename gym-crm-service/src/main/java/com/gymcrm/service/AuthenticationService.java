package com.gymcrm.service;

import com.gymcrm.dto.LoginRequest;
import com.gymcrm.security.CustomUserDetailsService;
import com.gymcrm.security.JwtService;
import com.gymcrm.security.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;

    public String login(LoginRequest request) {
        String username = request.getUsername();

        if (loginAttemptService.isBlocked(username)) {
            throw new RuntimeException("User is temporarily blocked due to too many failed login attempts.");
        }

        try {
            authenticate(username, request.getPassword());
        } catch (Exception ex) {
            loginAttemptService.loginFailed(username);
            throw ex;
        }

        loginAttemptService.loginSucceeded(username);

        org.springframework.security.core.userdetails.UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);

        return jwtService.generateToken(userDetails);
    }

    public void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(authToken);
    }
}
