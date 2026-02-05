package com.apora.eventweatherguard.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventForecastRequest {

    @NotBlank(message = "Event name is required")
    private String name;

    @NotNull(message = "Location is required")
    @Valid
    private LocationRequest location;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
}

