package com.bizzdesk.group.notification.service.kafka.interfaces;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface EmailVerificationChannel {

    @Input(value = "gotax-email-verification")
    SubscribableChannel input();
}
