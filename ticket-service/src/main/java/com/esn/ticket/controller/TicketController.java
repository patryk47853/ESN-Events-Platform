package com.esn.ticket.controller;

import com.esn.ticket.dto.CreateTicketRequest;
import com.esn.ticket.dto.TicketValidationResponse;
import com.esn.ticket.entity.Ticket;
import com.esn.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public Ticket createTicket(@RequestBody @Valid CreateTicketRequest request, JwtAuthenticationToken authentication
    ) {
        Long userId = extractUserId(authentication);

        return ticketService.createTicket(request, userId);
    }

    @GetMapping("/by-event/{eventId}")
    public List<Ticket> getTicketsByEvent(@PathVariable Long eventId) {
        return ticketService.getTicketsByEvent(eventId);
    }

    @PutMapping("/validate")
    public ResponseEntity<TicketValidationResponse> validateTicket(@RequestParam String token) {
        return ResponseEntity.ok(ticketService.validateTicket(token));
    }

    @GetMapping("/my")
    public List<Ticket> getMyTickets(JwtAuthenticationToken authentication) {
        Long userId = extractUserId(authentication);

        return ticketService.getTicketsForUser(userId);
    }

    private Long extractUserId(JwtAuthenticationToken authentication) {
        Number userIdClaim = authentication.getToken().getClaim("userId");

        if (userIdClaim == null) {
            throw new IllegalStateException("JWT does not contain userId");
        }

        return userIdClaim.longValue();
    }
}