package com.esn.ticket.service;

import com.esn.ticket.entity.Ticket;
import com.esn.ticket.entity.TicketStatus;
import com.esn.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketCleanupScheduler {

    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    // one minute
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cleanupExpiredTickets() {
        log.info("Starting cron job: Checking for expired pending tickets...");

        // limit: 15 minutes
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);

        List<Ticket> expiredTickets = ticketRepository.findByStatusAndCreatedAtBefore(
                TicketStatus.PENDING, 
                threshold
        );

        if (expiredTickets.isEmpty()) {
            log.info("No expired tickets found.");
            return;
        }

        log.info("Found {} expired tickets to cancel.", expiredTickets.size());

        for (Ticket ticket : expiredTickets) {
            try {
                ticketService.cancelTicket(ticket.getId(), "RESERVATION_TIMEOUT");
                log.info("Ticket ID: {} automatically cancelled due to timeout.", ticket.getId());
            } catch (Exception e) {
                log.error("Failed to automatically cancel ticket ID: {}", ticket.getId(), e);
            }
        }
    }
}