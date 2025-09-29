package com.gymcrm.cucumber;

import com.gymcrm.config.TestContainersConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import com.gymcrm.GymCrmRestApplication;


@CucumberContextConfiguration
@SpringBootTest(
        classes = GymCrmRestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = TestContainersConfig.Initializer.class)
@ActiveProfiles("test")
public class CrmCucumberSpringConfiguration {
}
