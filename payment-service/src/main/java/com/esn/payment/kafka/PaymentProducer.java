package com.esn.payment.kafka;

import com.esn.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate;

    public void sendPaymentSuccess(PaymentSuccessEvent event) {
        kafkaTemplate.send("payment-success-topic", event);
    }
}