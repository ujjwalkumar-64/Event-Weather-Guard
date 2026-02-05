package com.apora.eventweatherguard.service;

import com.apora.eventweatherguard.request.EventForecastRequest;
import com.apora.eventweatherguard.request.LocationRequest;
import com.apora.eventweatherguard.response.EventForecastResponse;
import com.apora.eventweatherguard.response.HourlyForecastResponse;
import com.apora.eventweatherguard.service.serviceImpl.EventForecastServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventForecastServiceImplTest {

    private WeatherApiClient weatherApiClient;
    private WeatherRuleEngine ruleEngine;
    private EventForecastServiceImpl service;

    @BeforeEach
    void setUp() {
        weatherApiClient = mock(WeatherApiClient.class);
        ruleEngine = mock(WeatherRuleEngine.class);
        service = new EventForecastServiceImpl(weatherApiClient, ruleEngine);
    }

    @Test
    void shouldThrowException_whenStartTimeIsAfterEndTime() {
        EventForecastRequest request = buildRequest(
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.evaluateEventForecast(request)
        );
    }

    @Test
    void shouldCallWeatherApiAndRuleEngine() {
        EventForecastRequest request = buildRequest(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );

        List<HourlyForecastResponse> forecasts = List.of(
                new HourlyForecastResponse(LocalTime.NOON, 10, 5)
        );

        when(weatherApiClient.getHourlyForecast(
                any(), any(), any())
        ).thenReturn(forecasts);

        EventForecastResponse expectedResponse =
                EventForecastResponse.builder().build();

        when(ruleEngine.evaluate(forecasts))
                .thenReturn(expectedResponse);

        EventForecastResponse actual =
                service.evaluateEventForecast(request);

        assertEquals(expectedResponse, actual);
        verify(weatherApiClient, times(1))
                .getHourlyForecast(any(), any(), any());
        verify(ruleEngine, times(1))
                .evaluate(forecasts);
    }

    private EventForecastRequest buildRequest(
            LocalDateTime start,
            LocalDateTime end) {

        LocationRequest location = new LocationRequest();
        location.setLatitude(19.0);
        location.setLongitude(72.0);

        EventForecastRequest request = new EventForecastRequest();
        request.setName("Test Event");
        request.setLocation(location);
        request.setStartTime(start);
        request.setEndTime(end);

        return request;
    }
}
