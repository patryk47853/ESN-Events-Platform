package com.esn.notification.kafka;

import com.esn.notification.event.PaymentSuccessEvent;
import com.esn.notification.event.TicketCreatedEvent;
import com.esn.notification.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Test
    void shouldProcessTicketCreatedEvent() {
        // Given
        TicketCreatedEvent event = TicketCreatedEvent.builder()
                .ticketId(1L)
                .eventId(100L)
                .userId(10L)
                .build();

        // When
        notificationConsumer.consumeTicketCreated(event);

        // Then
        verify(emailService, times(1))
                .sendTicketCreatedEmail(10L, 1L);
    }

    @Test
    void shouldProcessPaymentSuccessEvent() {
        // Given
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .ticketId(1L)
                .userId(10L)
                .build();

        // When
        notificationConsumer.consumePaymentSuccess(event);

        // Then
        verify(emailService, times(1))
                .sendPaymentSuccessEmail(10L, 1L);
    }

    @Test
    void shouldNotThrowExceptionWhenTicketEmailFails() {
        // Given
        TicketCreatedEvent event = TicketCreatedEvent.builder()
                .ticketId(1L)
                .eventId(100L)
                .userId(10L)
                .build();

        doThrow(new RuntimeException("SMTP Down"))
                .when(emailService)
                .sendTicketCreatedEmail(anyLong(), anyLong());

        // When & Then
        assertDoesNotThrow(() ->
                notificationConsumer.consumeTicketCreated(event));

        verify(emailService, times(1))
                .sendTicketCreatedEmail(10L, 1L);
    }

    @Test
    void shouldNotThrowExceptionWhenPaymentEmailFails() {
        // Given
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .ticketId(1L)
                .userId(10L)
                .build();

        doThrow(new RuntimeException("SMTP Down"))
                .when(emailService)
                .sendPaymentSuccessEmail(anyLong(), anyLong());

        // When & Then
        assertDoesNotThrow(() ->
                notificationConsumer.consumePaymentSuccess(event));

        verify(emailService, times(1))
                .sendPaymentSuccessEmail(10L, 1L);
    }
}