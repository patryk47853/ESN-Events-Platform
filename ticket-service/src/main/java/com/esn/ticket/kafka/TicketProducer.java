package com.esn.ticket.kafka;

import com.esn.common.event.TicketCancelledEvent;
import com.esn.common.event.TicketCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTicketCreated(TicketCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.TICKET_CREATED, event);
    }

    public void sendTicketCancelled(TicketCancelledEvent event) {
        kafkaTemplate.send(KafkaTopics.TICKET_CANCELLED, event);
    }
}