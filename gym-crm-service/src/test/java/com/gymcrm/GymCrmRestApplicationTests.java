package com.gymcrm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.NONE,
		classes = {GymCrmRestApplication.class, GymCrmRestApplicationTests.TestConfig.class},
		properties = {
				"spring.main.allow-bean-definition-overriding=true"
		}
)
class GymCrmRestApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
			return new HandlerMappingIntrospector();
		}
	}

	@Test
	void contextLoads() {
		// context should load successfully
	}

	@Test
	void mainMethod_shouldRunWithoutCrashing() {
		GymCrmRestApplication.main(new String[]{});
	}
}
