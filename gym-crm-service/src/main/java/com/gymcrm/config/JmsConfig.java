package com.gymcrm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.dto.WorkloadEventRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJms
public class JmsConfig {
    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter c = new MappingJackson2MessageConverter();
        c.setTargetType(MessageType.TEXT);
        c.setTypeIdPropertyName("_type");

        Map<String, Class<?>> ids = new HashMap<>();
        ids.put("workloadEvent", WorkloadEventRequest.class);
        c.setTypeIdMappings(ids);

        c.setObjectMapper(objectMapper);
        return c;
    }
}
