package com.esn.ticket.service;

import com.esn.ticket.entity.Ticket;
import com.esn.ticket.entity.TicketStatus;
import com.esn.ticket.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketCleanupSchedulerTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketCleanupScheduler scheduler;

    @Test
    void shouldCleanupExpiredTickets() {

        Ticket ticket = Ticket.builder()
                .id(1L)
                .status(TicketStatus.PENDING)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .build();

        when(ticketRepository.findByStatusAndCreatedAtBefore(
                eq(TicketStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of(ticket));

        scheduler.cleanupExpiredTickets();

        verify(ticketService)
                .cancelTicket(
                        1L,
                        "RESERVATION_TIMEOUT"
                );
    }

    @Test
    void shouldDoNothingWhenNoExpiredTicketsFound() {

        when(ticketRepository.findByStatusAndCreatedAtBefore(
                eq(TicketStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        scheduler.cleanupExpiredTickets();

        verify(ticketService, never())
                .cancelTicket(anyLong(), anyString());
    }
}