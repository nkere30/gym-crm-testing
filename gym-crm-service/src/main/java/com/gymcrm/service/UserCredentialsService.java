package com.gymcrm.service;

import com.gymcrm.util.CredentialUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserCredentialsService {

    public <T> String generateUsername(String firstName, String lastName, List<T> existingEntities, Function<T, String> usernameExtractor) {
        Set<String> usernames = existingEntities.stream()
                .map(usernameExtractor)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toSet());

        return CredentialUtil.generateUniqueUsername(firstName, lastName, usernames);
    }

    public String generatePassword() {
        return CredentialUtil.generateRandomPassword();
    }


}
