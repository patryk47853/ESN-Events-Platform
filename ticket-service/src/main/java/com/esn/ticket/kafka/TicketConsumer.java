package com.esn.ticket.kafka;

import com.esn.ticket.event.TicketCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TicketConsumer {

    @KafkaListener(topics = "ticket-created-topic", groupId = "ticket-group")
    public void consume(TicketCreatedEvent event) {

        System.out.println("Received event from Kafka:");
        System.out.println("Ticket ID: " + event.getTicketId());
        System.out.println("Event ID: " + event.getEventId());
        System.out.println("User ID: " + event.getUserId());
    }
}