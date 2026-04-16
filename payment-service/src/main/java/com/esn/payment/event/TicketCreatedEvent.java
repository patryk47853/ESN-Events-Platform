package com.esn.payment.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCreatedEvent {

    private Long ticketId;
    private Long eventId;
    private Long userId;
}