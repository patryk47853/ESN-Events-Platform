package com.esn.payment.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessEvent {

    private Long ticketId;
    private Long userId;
}