package com.esn.notification.kafka;

public final class KafkaTopics {

    public static final String TICKET_CREATED = "ticket-created-topic";
    public static final String TICKET_CANCELLED = "ticket-cancelled-topic";
    public static final String PAYMENT_SUCCESS = "payment-success-topic";
    public static final String PAYMENT_FAILED = "payment-failed-topic";

    private KafkaTopics() {
    }
}