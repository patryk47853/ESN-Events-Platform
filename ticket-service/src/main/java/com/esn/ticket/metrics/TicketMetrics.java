package com.esn.ticket.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TicketMetrics {

    private final Counter createdTickets;
    private final Counter confirmedTickets;
    private final Counter cancelledTickets;
    private final Counter reservationConflicts;

    public TicketMetrics(MeterRegistry meterRegistry) {
        this.createdTickets = Counter.builder("tickets.created")
                .description("Number of successfully created tickets")
                .register(meterRegistry);

        this.confirmedTickets = Counter.builder("tickets.confirmed")
                .description("Number of confirmed tickets")
                .register(meterRegistry);

        this.cancelledTickets = Counter.builder("tickets.cancelled")
                .description("Number of cancelled tickets")
                .register(meterRegistry);

        this.reservationConflicts = Counter.builder(
                        "tickets.reservation.conflicts"
                )
                .description(
                        "Number of rejected duplicate ticket reservations"
                )
                .register(meterRegistry);
    }

    public void incrementCreatedTickets() {
        createdTickets.increment();
    }

    public void incrementConfirmedTickets() {
        confirmedTickets.increment();
    }

    public void incrementCancelledTickets() {
        cancelledTickets.increment();
    }

    public void incrementReservationConflicts() {
        reservationConflicts.increment();
    }
}