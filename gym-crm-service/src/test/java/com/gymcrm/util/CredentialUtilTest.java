package com.gymcrm.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CredentialUtilTest {

    @Test
    void generateRandomPassword_shouldReturn10CharAlphanumericPassword() {
        String password = CredentialUtil.generateRandomPassword();

        assertNotNull(password);
        assertEquals(10, password.length());
        assertTrue(password.chars().allMatch(Character::isLetterOrDigit));
    }

    @Test
    void generateUniqueUsername_shouldReturnBaseIfNotTaken() {
        Set<String> existing = Set.of("john.doe1", "jane.smith");
        String result = CredentialUtil.generateUniqueUsername("john", "doe", existing);

        assertEquals("john.doe", result);
    }

    @Test
    void generateUniqueUsername_shouldReturnNumberedVariantIfTaken() {
        Set<String> existing = Set.of("john.doe", "john.doe1", "john.doe2");
        String result = CredentialUtil.generateUniqueUsername("john", "doe", existing);

        assertEquals("john.doe3", result);
    }

    @Test
    void extractUsernames_shouldExtractNonNullUsernames() {
        record DummyUser(String username) {}

        List<DummyUser> users = List.of(new DummyUser("a"), new DummyUser("b"), new DummyUser(null));
        Set<String> result = CredentialUtil.extractUsernames(users, DummyUser::username);

        assertEquals(Set.of("a", "b"), result);
    }
}
