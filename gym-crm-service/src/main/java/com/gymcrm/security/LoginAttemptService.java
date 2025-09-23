package com.gymcrm.security;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MINUTES = 5;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockedUsers = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attempts.remove(username);
        blockedUsers.remove(username);
    }

    public void loginFailed(String username) {
        attempts.merge(username, 1, Integer::sum);
        if (attempts.get(username) >= MAX_FAILED_ATTEMPTS) {
            blockedUsers.put(username, LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES));
        }
    }

    public boolean isBlocked(String username) {
        LocalDateTime blockedUntil = blockedUsers.get(username);
        if (blockedUntil == null) return false;
        if (blockedUntil.isBefore(LocalDateTime.now())) {
            // Unblock user
            blockedUsers.remove(username);
            attempts.remove(username);
            return false;
        }
        return true;
    }
}
