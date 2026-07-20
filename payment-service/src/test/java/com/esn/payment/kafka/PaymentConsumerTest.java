package com.esn.payment.kafka;

import com.esn.payment.event.PaymentSuccessEvent;
import com.esn.payment.event.TicketCreatedEvent;
import org.testng.annotations.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentConsumerTest {

    @Mock
    private PaymentProducer paymentProducer;

    @InjectMocks
    private PaymentConsumer paymentConsumer;

    @Test
    void shouldProcessTicketCreatedEventAndSendPaymentSuccess() {

        // Given
        TicketCreatedEvent event = TicketCreatedEvent.builder()
                .ticketId(1L)
                .eventId(100L)
                .userId(10L)
                .build();

        // When
        paymentConsumer.consume(event);

        // Then
        verify(paymentProducer, times(1))
                .sendPaymentSuccess(any(PaymentSuccessEvent.class));
    }

    @Test
    void shouldSendPaymentSuccessWithCorrectData() {

        // Given
        TicketCreatedEvent event = TicketCreatedEvent.builder()
                .ticketId(55L)
                .eventId(200L)
                .userId(99L)
                .build();

        // When
        paymentConsumer.consume(event);

        // Then
        verify(paymentProducer).sendPaymentSuccess(
                argThat(paymentEvent ->
                        paymentEvent.getTicketId().equals(55L)
                                && paymentEvent.getUserId().equals(99L)
                )
        );
    }
}