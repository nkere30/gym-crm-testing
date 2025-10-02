package com.gymcrm.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.MountableFile;
import org.testcontainers.containers.wait.strategy.Wait;

@TestConfiguration
public class TestContainersConfig {

    public static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:6.0");

    public static GenericContainer<?> activeMqContainer =
            new GenericContainer<>("rmohr/activemq:latest")
                    .withExposedPorts(61616, 8161);

    public static GenericContainer<?> workloadServiceContainer =
            new GenericContainer<>("openjdk:17-jdk")
                    .withExposedPorts(8081)
                    .withCopyFileToContainer(
                            MountableFile.forHostPath("workload-service/target/workload-service-1.0.0-SNAPSHOT.jar"),
                            "/app/workload-service.jar"
                    )
                    .waitingFor(Wait.forHttp("/actuator/health").forPort(8081))
                    .withCommand("java", "-jar", "/app/workload-service.jar");

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {

            mongoDBContainer.start();
            activeMqContainer.start();
            workloadServiceContainer.start();

            TestPropertyValues.of(
                    "spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl(),
                    "spring.activemq.broker-url=tcp://" +
                            activeMqContainer.getHost() + ":" + activeMqContainer.getMappedPort(61616),
                    "workload.base-url=http://" +
                            workloadServiceContainer.getHost() + ":" + workloadServiceContainer.getMappedPort(8081)
            ).applyTo(context.getEnvironment());
        }
    }
}
