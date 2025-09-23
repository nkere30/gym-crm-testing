package com.gymcrm.workload.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void workloadOpenApi_shouldDefineBearerAuth() {
        OpenAPI openAPI = config.workloadOpenApi();

        assertNotNull(openAPI);
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("bearerAuth"));
        assertTrue(openAPI.getSecurity().stream()
                .anyMatch(req -> req.containsKey("bearerAuth")));
    }
}
