package com.esn.notification.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCancelledEvent {

    private Long ticketId;
    private Long userId;
}