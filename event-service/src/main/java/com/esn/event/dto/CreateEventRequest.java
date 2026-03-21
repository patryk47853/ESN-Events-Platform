package com.esn.event.dto;

import com.esn.event.entity.EventType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String location;

    @NotNull
    private LocalDateTime date;

    @NotNull
    @Min(1)
    private Integer capacity;

    private Double price;

    @NotNull
    private Boolean isFree;

    @NotNull
    private EventType type;
}