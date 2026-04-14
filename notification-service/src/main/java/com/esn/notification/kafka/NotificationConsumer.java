package com.esn.notification.kafka;

import com.esn.notification.event.TicketCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    @KafkaListener(topics = "ticket-created-topic", groupId = "notification-group")
    public void consume(TicketCreatedEvent event) {

        System.out.println("Sending notification...");
        System.out.println("User [" + event.getUserId() +
                "] booked ticket for event: " + event.getEventId());
    }
}