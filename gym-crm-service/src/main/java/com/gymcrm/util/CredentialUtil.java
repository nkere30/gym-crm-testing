package com.gymcrm.util;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CredentialUtil {

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        return random.ints('0', 'z' + 1)
                .filter(Character::isLetterOrDigit)
                .limit(10)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    public static String generateUniqueUsername(String firstName, String lastName, Set<String> existingUsernames) {
        String base = firstName + "." + lastName;

        if (!existingUsernames.contains(base)) return base;

        return IntStream.range(1, Integer.MAX_VALUE)
                .mapToObj(i -> base + i)
                .filter(candidate -> !existingUsernames.contains(candidate))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to generate unique username"));
    }

    public static <T> Set<String> extractUsernames(List<T> entities, Function<T, String> usernameExtractor) {
        return entities.stream()
                .map(usernameExtractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
