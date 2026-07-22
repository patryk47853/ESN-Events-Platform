package com.esn.notification.kafka;

import com.esn.notification.event.PaymentSuccessEvent;
import com.esn.notification.event.TicketCancelledEvent;
import com.esn.notification.event.TicketCreatedEvent;
import com.esn.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = KafkaTopics.TICKET_CREATED)
    public void consumeTicketCreated(TicketCreatedEvent event) {
        log.info("Notification Service received TicketCreatedEvent for ticket ID: {}", event.getTicketId());
        try {
            emailService.sendTicketCreatedEmail(event.getUserId(), event.getTicketId());
        } catch (Exception e) {
            log.error("Failed to process email for created ticket ID: {}", event.getTicketId(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_SUCCESS)
    public void consumePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Notification Service received PaymentSuccessEvent for ticket ID: {}", event.getTicketId());
        try {
            emailService.sendPaymentSuccessEmail(event.getUserId(), event.getTicketId());
        } catch (Exception e) {
            log.error("Failed to process email for successful payment of ticket ID: {}", event.getTicketId(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.TICKET_CANCELLED)
    public void consumeTicketCancelled(TicketCancelledEvent event) {

        log.info("Notification Service received TicketCancelledEvent for ticket ID: {}", event.getTicketId());

        try {
            emailService.sendTicketCancelledEmail(event.getUserId(), event.getTicketId());
        } catch (Exception e) {
            log.error("Failed to process cancellation email for ticket ID: {}", event.getTicketId(), e);
        }
    }
}