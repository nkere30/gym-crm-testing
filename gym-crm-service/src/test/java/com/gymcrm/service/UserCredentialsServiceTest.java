package com.gymcrm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserCredentialsServiceTest {

    private UserCredentialsService userCredentialsService;

    @BeforeEach
    void setUp() {
        userCredentialsService = new UserCredentialsService();
    }

    @Test
    void generateUsername_shouldReturnBaseIfNotTaken() {
        record Dummy(String username) {}

        List<Dummy> users = List.of(new Dummy("john.doe1"), new Dummy("john.doe2"));
        String result = userCredentialsService.generateUsername("john", "doe", users, Dummy::username);

        assertEquals("john.doe", result);
    }

    @Test
    void generateUsername_shouldReturnIncrementedIfBaseTaken() {
        record Dummy(String username) {}

        List<Dummy> users = List.of(
                new Dummy("john.doe"),
                new Dummy("john.doe1"),
                new Dummy("john.doe2")
        );

        String result = userCredentialsService.generateUsername("john", "doe", users, Dummy::username);

        assertEquals("john.doe3", result);
    }

    @Test
    void generatePassword_shouldReturn10CharAlphanumericPassword() {
        String password = userCredentialsService.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
        assertTrue(password.chars().allMatch(Character::isLetterOrDigit));
    }
}
