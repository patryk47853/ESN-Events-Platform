package com.esn.payment.kafka;

import com.esn.payment.event.TicketCreatedEvent;
import com.esn.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentProducer paymentProducer;

    @KafkaListener(topics = "ticket-created-topic", groupId = "payment-group")
    public void consume(TicketCreatedEvent event) {

        System.out.println("Processing payment for ticket: " + event.getTicketId());

        // test purpose
        boolean paymentOk = true;

        if (paymentOk) {
            paymentProducer.sendPaymentSuccess(
                    PaymentSuccessEvent.builder()
                            .ticketId(event.getTicketId())
                            .userId(event.getUserId())
                            .build()
            );
        }
    }
}