package com.gymcrm.workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WorkloadServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkloadServiceApplication.class, args);
    }
}
