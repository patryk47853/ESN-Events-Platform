package com.esn.ticket.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {
    private Long ticketId;
    private Long userId;
    private String reason;
}