package com.esn.ticket.controller;

import com.esn.ticket.dto.CreateTicketRequest;
import com.esn.ticket.dto.TicketValidationResponse;
import com.esn.ticket.entity.Ticket;
import com.esn.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public Ticket createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @GetMapping("/by-event/{eventId}")
    public List<Ticket> getTicketsByEvent(@PathVariable Long eventId) {
        return ticketService.getTicketsByEvent(eventId);
    }

    @PutMapping("/validate")
    public ResponseEntity<TicketValidationResponse> validateTicket(@RequestParam String token) {
        return ResponseEntity.ok(ticketService.validateTicket(token));
    }
}