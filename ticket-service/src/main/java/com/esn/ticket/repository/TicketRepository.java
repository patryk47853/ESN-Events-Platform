package com.esn.ticket.repository;

import com.esn.ticket.entity.Ticket;
import com.esn.ticket.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByEventId(Long eventId);
    List<Ticket> findByStatusAndCreatedAtBefore(TicketStatus status, LocalDateTime dateTime);
    Optional<Ticket> findByTicketToken(String ticketToken);
    List<Ticket> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByEventIdAndUserIdAndStatusIn(Long eventId, Long userId, Collection<TicketStatus> statuses);
}