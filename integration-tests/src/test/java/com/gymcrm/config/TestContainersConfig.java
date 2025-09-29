package com.gymcrm.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration
public class TestContainersConfig {

    @Bean(destroyMethod = "stop")
    public MongoDBContainer mongoDBContainer() {
        MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");
        mongo.start();
        return mongo;
    }

    @Bean(destroyMethod = "stop")
    public GenericContainer<?> activeMqContainer() {
        GenericContainer<?> activeMq = new GenericContainer<>("rmohr/activemq:latest")
                .withExposedPorts(61616, 8161);
        activeMq.start();
        return activeMq;
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            MongoDBContainer mongo = context.getBean(MongoDBContainer.class);
            GenericContainer<?> activeMq = context.getBean(GenericContainer.class);

            TestPropertyValues.of(
                    "spring.data.mongodb.uri=" + mongo.getReplicaSetUrl(),
                    "spring.activemq.broker-url=tcp://" +
                    activeMq.getHost() + ":" + activeMq.getMappedPort(61616))
                    .applyTo(context.getEnvironment());
        }
    }
}
