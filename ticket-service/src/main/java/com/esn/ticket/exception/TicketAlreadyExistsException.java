package com.esn.ticket.exception;

public class TicketAlreadyExistsException extends RuntimeException {

    public TicketAlreadyExistsException(Long eventId) {
        super("User already has an active ticket for event: " + eventId);
    }
}