package com.esn.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendTicketCreatedEmail(Long userId, Long ticketId) {
        log.info("Sending email to Student (ID: {}): Your reservation for ticket #{} is PENDING.", userId, ticketId);
        log.info("Remember! You have 15 minutes to complete the payment, otherwise your spot will be released.");
    }

    public void sendPaymentSuccessEmail(Long userId, Long ticketId) {
        log.info("Sending email to Student (ID: {}): PAYMENT CONFIRMED for ticket #{}!", userId, ticketId);
        log.info("See you at the ESN event! Here is your booking confirmation.");
    }
}