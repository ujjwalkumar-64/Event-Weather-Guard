package com.apora.eventweatherguard.controller;

import com.apora.eventweatherguard.request.EventForecastRequest;
import com.apora.eventweatherguard.response.EventForecastResponse;
import com.apora.eventweatherguard.service.EventForecastService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class EventForecastController {

    private final EventForecastService eventForecastService;


    @PostMapping("/event-forecast")
    public ResponseEntity<EventForecastResponse> evaluateEventForecast(
            @Valid @RequestBody EventForecastRequest request) {

        return ResponseEntity.ok(
                eventForecastService.evaluateEventForecast(request)
        );
    }
}

