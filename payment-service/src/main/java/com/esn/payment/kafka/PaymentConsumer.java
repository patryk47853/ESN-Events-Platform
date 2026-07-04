package com.esn.payment.kafka;

import com.esn.payment.event.PaymentFailedEvent;
import com.esn.payment.event.TicketCreatedEvent;
import com.esn.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentProducer paymentProducer;

    @KafkaListener(topics = "ticket-created-topic", groupId = "payment-group")
    public void consume(TicketCreatedEvent event) {

        log.info("Processing payment for ticket: {}", event.getTicketId());

        // TODO:
        // This is a temporary mock payment implementation.
        // In production this should be replaced with a real
        // payment provider integration (Stripe, PayPal, etc.)
        boolean paymentOk = true;

        if (paymentOk) {

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