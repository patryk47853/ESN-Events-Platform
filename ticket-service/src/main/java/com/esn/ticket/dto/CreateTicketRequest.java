package com.esn.ticket.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTicketRequest {

    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;
}