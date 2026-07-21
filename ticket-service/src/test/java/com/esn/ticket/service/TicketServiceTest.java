package com.esn.ticket.service;

import com.esn.ticket.config.EventClient;
import com.esn.ticket.dto.CreateTicketRequest;
import com.esn.ticket.dto.TicketValidationResponse;
import com.esn.ticket.entity.Ticket;
import com.esn.ticket.entity.TicketStatus;
import com.esn.ticket.exception.EventNotFoundException;
import com.esn.ticket.kafka.TicketProducer;
import com.esn.ticket.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventClient eventClient;

    @Mock
    private TicketProducer ticketProducer;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void shouldCreateTicketSuccessfully() {

        CreateTicketRequest request = new CreateTicketRequest();
        request.setEventId(1L);
        request.setUserId(10L);

        when(eventClient.eventExists(1L))
                .thenReturn(true);

        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> {
                    Ticket ticket = invocation.getArgument(0);
                    ticket.setId(1L);
                    return ticket;
                });

        Ticket ticket = ticketService.createTicket(request);

        assertNotNull(ticket);
        assertEquals(TicketStatus.PENDING, ticket.getStatus());

        verify(eventClient).reserveSeat(1L);
        verify(ticketProducer).sendTicketCreated(any());
    }

    @Test
    void shouldThrowWhenEventDoesNotExist() {

        CreateTicketRequest request = new CreateTicketRequest();
        request.setEventId(999L);
        request.setUserId(10L);

        when(eventClient.eventExists(999L))
                .thenReturn(false);

        assertThrows(
                EventNotFoundException.class,
                () -> ticketService.createTicket(request)
        );

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldConfirmTicket() {

        Ticket ticket = Ticket.builder()
                .id(1L)
                .status(TicketStatus.PENDING)
                .build();

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        ticketService.confirmTicket(1L);

        assertEquals(
                TicketStatus.CONFIRMED,
                ticket.getStatus()
        );

        assertNotNull(ticket.getTicketToken());

        verify(ticketRepository).save(ticket);
    }

    @Test
    void shouldThrowWhenConfirmingCancelledTicket() {

        Ticket ticket = Ticket.builder()
                .id(1L)
                .status(TicketStatus.CANCELLED)
                .build();

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                IllegalStateException.class,
                () -> ticketService.confirmTicket(1L)
        );
    }

    @Test
    void shouldCancelPendingTicket() {

        Ticket ticket = Ticket.builder()
                .id(1L)
                .eventId(5L)
                .userId(10L)
                .status(TicketStatus.PENDING)
                .build();

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        ticketService.cancelTicket(
                1L,
                "PAYMENT_TIMEOUT"
        );

        assertEquals(
                TicketStatus.CANCELLED,
                ticket.getStatus()
        );

        verify(eventClient)
                .releaseSeat(5L);

        verify(ticketProducer)
                .sendTicketCancelled(any());
    }

    @Test
    void shouldThrowWhenAlreadyCancelled() {

        Ticket ticket = Ticket.builder()
                .id(1L)
                .status(TicketStatus.CANCELLED)
                .build();

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                IllegalStateException.class,
                () -> ticketService.cancelTicket(
                        1L,
                        "TEST"
                )
        );
    }

    @Test
    void shouldThrowWhenCancellingConfirmedTicket() {

        Ticket ticket = Ticket.builder()
                .id(1L)
                .status(TicketStatus.CONFIRMED)
                .build();

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                IllegalStateException.class,
                () -> ticketService.cancelTicket(
                        1L,
                        "TEST"
                )
        );
    }

    @Test
    void shouldValidateConfirmedTicket() {

        Ticket ticket = Ticket.builder()
                .ticketToken("TOKEN123")
                .status(TicketStatus.CONFIRMED)
                .userId(10L)
                .eventId(5L)
                .isUsed(false)
                .build();

        when(ticketRepository.findByTicketToken("TOKEN123"))
                .thenReturn(Optional.of(ticket));

        TicketValidationResponse response =
                ticketService.validateTicket("TOKEN123");

        assertEquals(
                "VALID",
                response.getStatus()
        );

        verify(ticketRepository)
                .save(ticket);
    }

    @Test
    void shouldRejectUsedTicket() {

        Ticket ticket = Ticket.builder()
                .ticketToken("TOKEN123")
                .status(TicketStatus.CONFIRMED)
                .isUsed(true)
                .build();

        when(ticketRepository.findByTicketToken("TOKEN123"))
                .thenReturn(Optional.of(ticket));

        TicketValidationResponse response =
                ticketService.validateTicket("TOKEN123");

        assertEquals(
                "INVALID",
                response.getStatus()
        );
    }

    @Test
    void shouldRejectPendingTicket() {

        Ticket ticket = Ticket.builder()
                .ticketToken("TOKEN123")
                .status(TicketStatus.PENDING)
                .build();

        when(ticketRepository.findByTicketToken("TOKEN123"))
                .thenReturn(Optional.of(ticket));

        TicketValidationResponse response =
                ticketService.validateTicket("TOKEN123");

        assertEquals(
                "INVALID",
                response.getStatus()
        );
    }

    @Test
    void shouldReturnInvalidForUnknownToken() {

        when(ticketRepository.findByTicketToken("INVALID"))
                .thenReturn(Optional.empty());

        TicketValidationResponse response =
                ticketService.validateTicket("INVALID");

        assertEquals(
                "INVALID",
                response.getStatus()
        );
    }
}