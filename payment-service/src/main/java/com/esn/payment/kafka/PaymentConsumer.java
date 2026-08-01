package com.esn.payment.kafka;

import com.esn.common.event.PaymentFailedEvent;
import com.esn.common.event.PaymentSuccessEvent;
import com.esn.common.event.TicketCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentProducer paymentProducer;

    @KafkaListener(topics = KafkaTopics.TICKET_CREATED, groupId = KafkaGroups.PAYMENT_SERVICE)
    public void consume(TicketCreatedEvent event) {

        log.info("Processing payment for ticket: {}", event.getTicketId());

        // TODO:
        // This is a temporary mock payment implementation.
        // In production this should be replaced with a real
        // payment provider integration (Bank Transfer, PayPal, etc.)
        // Right now: 50% chance for payment success
        boolean paymentSuccessful = Math.random() > 0.5;

        if (paymentSuccessful) {
            paymentProducer.sendPaymentSuccess(
                    PaymentSuccessEvent.builder()
                            .ticketId(event.getTicketId())
                            .userId(event.getUserId())
                            .build()
            );

        } else {
            paymentProducer.sendPaymentFailed(
                    PaymentFailedEvent.builder()
                            .ticketId(event.getTicketId())
                            .userId(event.getUserId())
                            .reason("PAYMENT_REJECTED")
                            .build()
            );
        }
    }
}