package com.apora.eventweatherguard.service.serviceImpl;

import com.apora.eventweatherguard.response.Classification;
import com.apora.eventweatherguard.service.EventForecastService;
import com.apora.eventweatherguard.service.WeatherApiClient;
import com.apora.eventweatherguard.service.WeatherRuleEngine;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.apora.eventweatherguard.request.EventForecastRequest;
import com.apora.eventweatherguard.response.EventForecastResponse;
import com.apora.eventweatherguard.response.HourlyForecastResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
public class EventForecastServiceImpl implements EventForecastService {

    private final WeatherApiClient weatherApiClient;
    private final WeatherRuleEngine ruleEngine;



    @Override
    public EventForecastResponse evaluateEventForecast(
            EventForecastRequest request) {

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException(
                    "Start time must be before end time"
            );
        }

        List<HourlyForecastResponse> hourlyForecast =
                weatherApiClient.getHourlyForecast(
                        request.getLocation(),
                        request.getStartTime(),
                        request.getEndTime()
                );

        EventForecastResponse response =
                ruleEngine.evaluate(hourlyForecast);

        List<HourlyForecastResponse> next24HoursForecast =
                weatherApiClient.getHourlyForecast(
                        request.getLocation(),
                        request.getStartTime(),
                        request.getStartTime().plusHours(24)
                );

        //  BONUS PART CALLED HERE
        if (response.getClassification() != Classification.SAFE) {

            Duration eventDuration = Duration.between(
                    request.getStartTime(),
                    request.getEndTime()
            );

            ruleEngine
                    .recommendTimeWindow(next24HoursForecast, eventDuration)
                    .ifPresent(response::setRecommendedWindow);
        }

        return response;
    }

}

