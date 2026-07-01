package com.esn.event.controller;

import com.esn.event.dto.CreateEventRequest;
import com.esn.event.dto.EventFinancialReport;
import com.esn.event.entity.Event;
import com.esn.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public Event createEvent(@Valid @RequestBody CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/{id}/finances")
    public ResponseEntity<EventFinancialReport> getFinancialReport(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getFinancialReport(id));
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<Event> updateEventPrice(@PathVariable Long id, @RequestParam double price) {
        return ResponseEntity.ok(eventService.updateEventPrice(id, price));
    }
}