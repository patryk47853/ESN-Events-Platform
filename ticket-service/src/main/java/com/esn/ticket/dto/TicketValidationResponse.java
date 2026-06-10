package com.esn.ticket.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketValidationResponse {
    private String status;
    private String message;
    private Long userId;
    private Long eventId;
}