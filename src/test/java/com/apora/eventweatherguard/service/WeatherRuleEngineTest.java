package com.apora.eventweatherguard.service;

import com.apora.eventweatherguard.response.Classification;
import com.apora.eventweatherguard.response.EventForecastResponse;
import com.apora.eventweatherguard.response.HourlyForecastResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherRuleEngineTest {

    private WeatherRuleEngine ruleEngine;

    @BeforeEach
    void setUp() {
        ruleEngine = new WeatherRuleEngine();
    }

    @Test
    void shouldReturnUnsafe_whenHeavyRainPresent() {
        List<HourlyForecastResponse> forecasts = List.of(
                new HourlyForecastResponse(LocalTime.of(17, 0), 90, 10)
        );

        EventForecastResponse response = ruleEngine.evaluate(forecasts);

        assertEquals(Classification.UNSAFE, response.getClassification());
    }

    @Test
    void shouldReturnRisky_whenHighRainProbabilityPresent() {
        List<HourlyForecastResponse> forecasts = List.of(
                new HourlyForecastResponse(LocalTime.of(18, 0), 65, 10)
        );

        EventForecastResponse response = ruleEngine.evaluate(forecasts);

        assertEquals(Classification.RISKY, response.getClassification());
    }

    @Test
    void shouldReturnSafe_whenConditionsAreStable() {
        List<HourlyForecastResponse> forecasts = List.of(
                new HourlyForecastResponse(LocalTime.of(19, 0), 10, 5)
        );

        EventForecastResponse response = ruleEngine.evaluate(forecasts);

        assertEquals(Classification.SAFE, response.getClassification());
    }

    @Test
    void shouldReturnSafe_whenForecastListIsEmpty() {
        EventForecastResponse response = ruleEngine.evaluate(List.of());

        assertEquals(Classification.SAFE, response.getClassification());
    }
}
