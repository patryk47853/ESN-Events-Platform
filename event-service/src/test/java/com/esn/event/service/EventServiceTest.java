package com.esn.event.service;

import com.esn.event.dto.CreateEventRequest;
import com.esn.event.entity.Event;
import com.esn.event.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventServiceTest {

    private final EventRepository repository = Mockito.mock(EventRepository.class);
    private final EventService service = new EventService(repository);

    @Test
    void shouldCreateFreeEventWithZeroPrice() {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Test");
        request.setLocation("Porto");
        request.setCapacity(10);
        request.setIsFree(true);

        Event event = service.createEvent(request);

        assertEquals(0.0, event.getPrice());
    }
}