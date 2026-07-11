package com.esn.payment.dto;

import com.esn.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResult {

    private Long ticketId;
    private Long userId;
    private PaymentStatus status;
}