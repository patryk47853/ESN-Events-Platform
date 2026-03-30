package com.esn.ticket.kafka;

import com.esn.ticket.event.TicketCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketProducer {

    private final KafkaTemplate<String, TicketCreatedEvent> kafkaTemplate;

    public void sendTicketCreated(TicketCreatedEvent event) {
        kafkaTemplate.send("ticket-created-topic", event);
    }
}