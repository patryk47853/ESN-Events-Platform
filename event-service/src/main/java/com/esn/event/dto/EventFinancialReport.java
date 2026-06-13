package com.esn.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventFinancialReport {
    private Long eventId;
    private String eventTitle;
    private double ticketPrice;
    private int ticketsSold;
    private double currentRevenue;    // price * bookedSeats
    private double potentialRevenue;  // price * capacity (sold out)
}