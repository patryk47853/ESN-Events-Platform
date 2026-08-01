package com.esn.ticket.kafka;

import com.esn.common.event.TicketCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TicketConsumer {

    @KafkaListener(topics = KafkaTopics.TICKET_CREATED, groupId = KafkaGroups.TICKET_SERVICE)
    public void consume(TicketCreatedEvent event) {

        log.info("Received event from Kafka:");
        log.info("Ticket ID: {}", event.getTicketId());
        log.info("Event ID: {}", event.getEventId());
        log.info("User ID: {}", event.getUserId());
    }
}