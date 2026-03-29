package com.esn.event.service;

import com.esn.event.dto.CreateEventRequest;
import com.esn.event.entity.Event;
import com.esn.event.exception.EventNotFoundException;
import com.esn.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(CreateEventRequest request) {

        if (request.getIsFree()) {
            request.setPrice(0.0);
        } else {
            if (request.getPrice() == null || request.getPrice() <= 0) {
                throw new IllegalArgumentException("Paid event must have price > 0");
            }
        }

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .date(request.getDate())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .isFree(request.getIsFree())
                .type(request.getType())
                .build();

        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }
}