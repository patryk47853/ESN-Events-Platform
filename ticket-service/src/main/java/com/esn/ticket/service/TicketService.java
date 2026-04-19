package com.esn.ticket.service;

import com.esn.ticket.config.EventClient;
import com.esn.ticket.dto.CreateTicketRequest;
import com.esn.ticket.entity.Ticket;
import com.esn.ticket.entity.TicketStatus;
import com.esn.ticket.event.TicketCreatedEvent;
import com.esn.ticket.exception.EventNotFoundException;
import com.esn.ticket.kafka.TicketProducer;
import com.esn.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventClient eventClient;
    private final TicketProducer ticketProducer;

    public Ticket createTicket(CreateTicketRequest request) {

        if (!eventClient.eventExists(request.getEventId())) {
            throw new EventNotFoundException(request.getEventId());
        }

        Ticket ticket = ticketRepository.save(
                Ticket.builder()
                        .eventId(request.getEventId())
                        .userId(request.getUserId())
                        .status(TicketStatus.PENDING)
                        .build()
        );

        ticketProducer.sendTicketCreated(
                TicketCreatedEvent.builder()
                        .ticketId(ticket.getId())
                        .eventId(ticket.getEventId())
                        .userId(ticket.getUserId())
                        .build()
        );

        return ticket;
    }

    public void confirmTicket(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(TicketStatus.CONFIRMED);

        ticketRepository.save(ticket);

        System.out.println("Ticket with ID: [" + ticketId + "] confirmed");
    }
}