package com.gymcrm.cucumber;

import com.gymcrm.config.TestContainersConfig;
import com.gymcrm.workload.WorkloadServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(
        classes = WorkloadServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = TestContainersConfig.Initializer.class)
@ActiveProfiles("test")
public class WorkloadCucumberSpringConfiguration {
}
