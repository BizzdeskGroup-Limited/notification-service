package com.bizzdesk.group.notification.service;

import com.bizzdesk.group.notification.service.kafka.interfaces.EmailVerificationChannel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({EmailVerificationChannel.class})
public class NotificationServiceConfiguration {

    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
