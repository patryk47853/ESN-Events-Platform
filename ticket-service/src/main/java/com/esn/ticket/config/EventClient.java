package com.esn.ticket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EventClient {

    private final RestTemplate restTemplate;

    @Value("${event-service.url}")
    private String eventServiceUrl;

    public boolean eventExists(Long eventId) {
        try {
            restTemplate.getForObject(
                    eventServiceUrl + eventId,
                    Object.class
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void reserveSeat(Long eventId) {
        restTemplate.put(eventServiceUrl + eventId + "/reserve", null);
    }

    public void releaseSeat(Long eventId) {
        restTemplate.put(eventServiceUrl + eventId + "/release", null);
    }
}