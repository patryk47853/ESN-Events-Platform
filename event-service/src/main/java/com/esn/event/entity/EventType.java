package com.esn.event.entity;

import lombok.Getter;

@Getter
public enum EventType {

    SPORT("Sport event"),
    NIGHT("Night life"),
    CULTURAL("Cultural"),
    SOCIAL("Social"),
    TRIP("Trip"),
    EDUCATIONAL("Educational");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

}