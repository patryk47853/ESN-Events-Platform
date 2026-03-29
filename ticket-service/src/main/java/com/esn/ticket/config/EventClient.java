package com.esn.ticket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EventClient {

    private final RestTemplate restTemplate;

    public boolean eventExists(Long eventId) {
        try {
            restTemplate.getForObject(
                    "http://localhost:8081/events/" + eventId,
                    Object.class
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}