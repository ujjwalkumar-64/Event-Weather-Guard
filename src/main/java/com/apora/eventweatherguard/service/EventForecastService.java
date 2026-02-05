package com.apora.eventweatherguard.service;

import com.apora.eventweatherguard.request.EventForecastRequest;
import com.apora.eventweatherguard.response.EventForecastResponse;
import org.springframework.stereotype.Service;

@Service
public interface EventForecastService {
    EventForecastResponse evaluateEventForecast(EventForecastRequest request);
}
