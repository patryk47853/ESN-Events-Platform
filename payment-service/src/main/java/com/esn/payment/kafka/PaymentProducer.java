package com.esn.payment.kafka;

import com.esn.payment.event.PaymentFailedEvent;
import com.esn.payment.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentSuccess(PaymentSuccessEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_SUCCESS, event);
    }

    public void sendPaymentFailed(PaymentFailedEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, event);
    }
}