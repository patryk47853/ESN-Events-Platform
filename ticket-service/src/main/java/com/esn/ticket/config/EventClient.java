package com.esn.ticket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EventClient {

    private final RestTemplate restTemplate;

    private static final String EVENT_SERVICE_URL = "http://localhost:8081/api/events/";

    public boolean eventExists(Long eventId) {
        try {
            restTemplate.getForObject(
                    EVENT_SERVICE_URL + eventId,
                    Object.class
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}