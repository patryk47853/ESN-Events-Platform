package com.esn.event.service;

import com.esn.event.dto.CreateEventRequest;
import com.esn.event.dto.EventFinancialReport;
import com.esn.event.entity.Event;
import com.esn.event.exception.EventFullException;
import com.esn.event.exception.EventNotFoundException;
import com.esn.event.repository.EventRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void reserveSeat(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getBookedSeats() >= event.getCapacity()) {
            throw new EventFullException("No seats available for event: " + event.getTitle());
        }

        event.setBookedSeats(event.getBookedSeats() + 1);
        eventRepository.save(event);
    }

    @Transactional
    public void releaseSeat(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getBookedSeats() > 0) {
            event.setBookedSeats(event.getBookedSeats() - 1);
            eventRepository.save(event);
        }
    }

    public EventFinancialReport getFinancialReport(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        double currentRevenue = event.getPrice() * event.getBookedSeats();
        double potentialRevenue = event.getPrice() * event.getCapacity();

        return EventFinancialReport.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .ticketPrice(event.getPrice())
                .ticketsSold(event.getBookedSeats())
                .currentRevenue(currentRevenue)
                .potentialRevenue(potentialRevenue)
                .build();
    }

    public Event updateEventPrice(Long eventId, double newPrice) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (Boolean.TRUE.equals(event.getIsFree())) {
            throw new IllegalStateException("Cannot change price of a FREE event! Change 'isFree' status first.");
        }

        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative!");
        }

        event.setPrice(newPrice);
        return eventRepository.save(event);
    }
}