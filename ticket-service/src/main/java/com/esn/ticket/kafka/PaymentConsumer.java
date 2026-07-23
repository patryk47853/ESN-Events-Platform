package com.esn.ticket.kafka;

import com.esn.ticket.event.PaymentFailedEvent;
import com.esn.ticket.event.PaymentSuccessEvent;
import com.esn.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final TicketService ticketService;

    @KafkaListener(topics = KafkaTopics.PAYMENT_SUCCESS, groupId = KafkaGroups.TICKET_SERVICE)
    public void consume(PaymentSuccessEvent event) {
        log.info("Payment received for ticket ID: {}", event.getTicketId());

        try {
            ticketService.confirmTicket(event.getTicketId());
            log.info("Ticket ID: {} has been successfully confirmed.", event.getTicketId());
        } catch (Exception e) {
            log.error("Failed to confirm ticket ID: {}", event.getTicketId(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = KafkaGroups.TICKET_SERVICE)
    public void consumePaymentFailed(PaymentFailedEvent event) {
        log.info("Received payment failed event for ticket ID: {}. Reason: {}", event.getTicketId(), event.getReason());

        try {
            ticketService.cancelTicket(event.getTicketId(), "PAYMENT_REJECTED_" + event.getReason());
            log.info("Ticket ID: {} has been successfully cancelled due to failed payment.", event.getTicketId());
        } catch (Exception e) {
            log.error("Failed to process payment failure for ticket ID: {}", event.getTicketId(), e);
        }
    }
}