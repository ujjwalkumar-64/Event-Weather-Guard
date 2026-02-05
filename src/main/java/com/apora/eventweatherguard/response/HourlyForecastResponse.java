package com.apora.eventweatherguard.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class HourlyForecastResponse {

    private LocalTime time;
    private int rainProbability;
    private double windKmh;
}
