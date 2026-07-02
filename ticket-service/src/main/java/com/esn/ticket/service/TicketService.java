package com.esn.ticket.service;

import com.esn.ticket.config.EventClient;
import com.esn.ticket.dto.CreateTicketRequest;
import com.esn.ticket.dto.TicketValidationResponse;
import com.esn.ticket.entity.Ticket;
import com.esn.ticket.entity.TicketStatus;
import com.esn.ticket.event.TicketCancelledEvent;
import com.esn.ticket.event.TicketCreatedEvent;
import com.esn.ticket.exception.EventNotFoundException;
import com.esn.ticket.exception.TicketNotFoundException;
import com.esn.ticket.kafka.TicketProducer;
import com.esn.ticket.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventClient eventClient;
    private final TicketProducer ticketProducer;

    @Transactional
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

        eventClient.reserveSeat(request.getEventId());

        ticketProducer.sendTicketCreated(
                TicketCreatedEvent.builder()
                        .ticketId(ticket.getId())
                        .eventId(ticket.getEventId())
                        .userId(ticket.getUserId())
                        .build()
        );

        return ticket;
    }

    @Transactional
    public void confirmTicket(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING tickets can be confirmed"
            );
        }

        ticket.setStatus(TicketStatus.CONFIRMED);

        ticket.setStatus(TicketStatus.CONFIRMED);
        ticket.setTicketToken("ESN-QR-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        ticketRepository.save(ticket);

        System.out.println("Ticket with ID: [" + ticketId + "] confirmed");
    }

    public List<Ticket> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    @Transactional
    public void cancelTicket(Long ticketId, String reason) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("Ticket is already cancelled");
        }

        if (ticket.getStatus() == TicketStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Confirmed tickets cannot be cancelled"
            );
        }

        ticket.setStatus(TicketStatus.CANCELLED);

        eventClient.releaseSeat(ticket.getEventId());

        ticketProducer.sendTicketCancelled(
                TicketCancelledEvent.builder()
                        .ticketId(ticket.getId())
                        .eventId(ticket.getEventId())
                        .userId(ticket.getUserId())
                        .reason(reason)
                        .build()
        );
    }

    public TicketValidationResponse validateTicket(String token) {


        return ticketRepository.findByTicketToken(token)
                .map(ticket -> {
                    if (ticket.isUsed()) {
                        return TicketValidationResponse.builder()
                                .status("INVALID")
                                .message("Ticket has already been used!")
                                .build();
                    }

                    if (ticket.getStatus() != TicketStatus.CONFIRMED) {
                        return TicketValidationResponse.builder()
                                .status("INVALID")
                                .message("Ticket is not paid! Current status: " + ticket.getStatus())
                                .build();
                    }

                    ticket.setUsed(true);
                    ticketRepository.save(ticket);

                    return TicketValidationResponse.builder()
                            .status("VALID")
                            .message("Ticket is valid. Welcome aboard!")
                            .userId(ticket.getUserId())
                            .eventId(ticket.getEventId())
                            .build();
                })
                .orElse(TicketValidationResponse.builder()
                        .status("INVALID")
                        .message("Ticket token does not exist!")
                        .build());
    }
}