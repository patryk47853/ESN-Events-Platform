package com.esn.event.service;

import com.esn.event.dto.CreateEventRequest;
import com.esn.event.entity.Event;
import com.esn.event.exception.EventFullException;
import com.esn.event.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldCreateFreeEventWithZeroPrice() {
        // Given
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Test");
        request.setLocation("Porto");
        request.setCapacity(10);
        request.setIsFree(true);
        // random price, just to make sure it will become free
        request.setPrice(99.99);

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event argument = invocation.getArgument(0);
            argument.setId(1L);
            return argument;
        });

        // When
        Event event = eventService.createEvent(request);

        // Then
        assertNotNull(event);
        assertTrue(event.getIsFree());
        assertEquals(0.0, event.getPrice(), "Price must be exactly 0.0 for free events!");
        verify(eventRepository, times(1)).save(any(Event.class));
    }
}