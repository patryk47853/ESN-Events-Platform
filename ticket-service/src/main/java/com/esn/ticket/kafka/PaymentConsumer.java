package com.esn.ticket.kafka;

import com.esn.ticket.event.PaymentSuccessEvent;
import com.esn.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final TicketService ticketService;

    @KafkaListener(topics = "payment-success-topic", groupId = "ticket-group")
    public void consume(PaymentSuccessEvent event) {

        System.out.println("Payment received for ticket: " + event.getTicketId());

        ticketService.confirmTicket(event.getTicketId());
    }
}