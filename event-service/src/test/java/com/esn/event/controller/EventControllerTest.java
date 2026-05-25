package com.esn.event.controller;

import com.esn.event.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.esn.event.dto.CreateEventRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn400WhenInvalidRequest() throws Exception {
        CreateEventRequest request = new CreateEventRequest();

        mockMvc.perform(post("/events")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}