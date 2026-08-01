package com.esn.common.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketCreatedEvent {

    private Long ticketId;
    private Long eventId;
    private Long userId;
}