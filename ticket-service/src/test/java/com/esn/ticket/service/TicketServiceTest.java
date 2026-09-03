package com.esn.ticket.service;

import com.esn.common.event.TicketCreatedEvent;
import com.esn.ticket.config.EventClient;
import com.esn.ticket.dto.CreateTicketRequest;
import com.esn.ticket.dto.TicketValidationResponse;
import com.esn.ticket.entity.Ticket;
import com.esn.ticket.entity.TicketStatus;
import com.esn.ticket.exception.EventNotFoundException;
import com.esn.ticket.exception.TicketAlreadyExistsException;
import com.esn.ticket.kafka.TicketProducer;
import com.esn.ticket.metrics.TicketMetrics;
import com.esn.ticket.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
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

    @Mock
    private TicketMetrics ticketMetrics;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void shouldCreateTicketSuccessfully() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setEventId(1L);

        Long userId = 10L;

        when(eventClient.eventExists(1L))
                .thenReturn(true);

        when(ticketRepository.existsByEventIdAndUserIdAndStatusIn(
                eq(1L),
                eq(userId),
                anyCollection()
        )).thenReturn(false);

        when(ticketRepository.saveAndFlush(any(Ticket.class)))
                .thenAnswer(invocation -> {
                    Ticket ticket = invocation.getArgument(0);
                    ticket.setId(1L);
                    return ticket;
                });

        Ticket ticket = ticketService.createTicket(request, userId);

        assertNotNull(ticket);
        assertEquals(1L, ticket.getEventId());
        assertEquals(10L, ticket.getUserId());
        assertEquals(TicketStatus.PENDING, ticket.getStatus());

        verify(eventClient).reserveSeat(1L);
        verify(ticketProducer).sendTicketCreated(any(TicketCreatedEvent.class));
        verify(ticketMetrics).incrementCreatedTickets();
        verify(ticketMetrics, never()).incrementReservationConflicts();
    }

    @Test
    void shouldThrowWhenEventDoesNotExist() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setEventId(999L);

        Long userId = 10L;

        when(eventClient.eventExists(999L))
                .thenReturn(false);

        assertThrows(
                EventNotFoundException.class,
                () -> ticketService.createTicket(request, userId)
        );

        verify(ticketRepository, never()).save(any());
        verify(eventClient, never()).reserveSeat(anyLong());
        verify(ticketProducer, never()).sendTicketCreated(any());
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

    @Test
    void shouldReturnTicketsForAuthenticatedUser() {
        Long userId = 10L;

        Ticket ticket = Ticket.builder()
                .id(1L)
                .eventId(1L)
                .userId(userId)
                .status(TicketStatus.CONFIRMED)
                .build();

        when(ticketRepository.findAllByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(ticket));

        List<Ticket> tickets =
                ticketService.getTicketsForUser(userId);

        assertEquals(1, tickets.size());
        assertEquals(userId, tickets.getFirst().getUserId());

        verify(ticketRepository)
                .findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void shouldRejectDuplicateActiveTicket() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setEventId(1L);

        Long userId = 10L;

        when(eventClient.eventExists(1L))
                .thenReturn(true);

        when(ticketRepository.existsByEventIdAndUserIdAndStatusIn(
                eq(1L),
                eq(userId),
                anyCollection()
        )).thenReturn(true);

        assertThrows(
                TicketAlreadyExistsException.class,
                () -> ticketService.createTicket(request, userId)
        );

        verify(ticketRepository, never()).save(any());
        verify(eventClient, never()).reserveSeat(anyLong());
        verify(ticketProducer, never()).sendTicketCreated(any());
        verify(ticketMetrics).incrementReservationConflicts();
        verify(ticketMetrics, never()).incrementCreatedTickets();
    }

    @Test
    void shouldRejectDuplicateTicketWhenDatabaseConstraintIsViolated() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setEventId(1L);

        Long userId = 10L;

        when(eventClient.eventExists(1L))
                .thenReturn(true);

        when(ticketRepository.existsByEventIdAndUserIdAndStatusIn(
                eq(1L),
                eq(userId),
                anyCollection()
        )).thenReturn(false);

        when(ticketRepository.saveAndFlush(any(Ticket.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "Unique active ticket constraint violated"
                ));

        assertThrows(
                TicketAlreadyExistsException.class,
                () -> ticketService.createTicket(request, userId)
        );

        verify(eventClient, never()).reserveSeat(anyLong());
        verify(ticketProducer, never()).sendTicketCreated(any());
        verify(ticketMetrics).incrementReservationConflicts();
        verify(ticketMetrics, never()).incrementCreatedTickets();
    }
}