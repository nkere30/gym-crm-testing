package com.gymcrm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    void isBlocked_shouldReturnFalseWhenNotBlocked() {
        assertFalse(loginAttemptService.isBlocked("user1"));
    }

    @Test
    void isBlocked_shouldReturnTrueAfterMaxFailedAttempts() {
        String username = "user2";
        for (int i = 0; i < 3; i++) {
            loginAttemptService.loginFailed(username);
        }
        assertTrue(loginAttemptService.isBlocked(username));
    }

    @Test
    void loginSucceeded_shouldUnblockUser() {
        String username = "user3";
        for (int i = 0; i < 3; i++) {
            loginAttemptService.loginFailed(username);
        }
        assertTrue(loginAttemptService.isBlocked(username));
        loginAttemptService.loginSucceeded(username);
        assertFalse(loginAttemptService.isBlocked(username));
    }
}
