package com.apora.eventweatherguard.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventForecastResponse {

    private Classification classification;
    private String summary;
    private List<String> reason;
    private List<HourlyForecastResponse> eventWindowForecast;
}

