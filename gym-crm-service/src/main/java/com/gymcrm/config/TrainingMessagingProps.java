package com.gymcrm.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.messaging.training")
public class TrainingMessagingProps {
    private String queue;
    private String dlq = "training.events.dlq";
    private Integer maxRedeliveries = 3;
}
