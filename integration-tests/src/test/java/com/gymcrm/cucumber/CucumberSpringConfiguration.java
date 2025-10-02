package com.gymcrm.cucumber;

import com.gymcrm.GymCrmRestApplication;
import com.gymcrm.config.TestContainersConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(
        classes = GymCrmRestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(
        initializers = TestContainersConfig.Initializer.class
)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
