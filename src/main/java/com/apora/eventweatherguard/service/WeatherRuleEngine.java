package com.apora.eventweatherguard.service;

import com.apora.eventweatherguard.response.Classification;
import com.apora.eventweatherguard.response.EventForecastResponse;
import com.apora.eventweatherguard.response.HourlyForecastResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherRuleEngine {

    private static final int UNSAFE_RAIN_THRESHOLD = 80;
    private static final double UNSAFE_WIND_THRESHOLD = 40.0;
    private static final int RISKY_RAIN_THRESHOLD = 60;

    public EventForecastResponse evaluate(
            List<HourlyForecastResponse> forecasts) {

        if (forecasts == null || forecasts.isEmpty()) {
            return buildResponse(
                    Classification.SAFE,
                    List.of("No adverse weather conditions detected"),
                    forecasts
            );
        }

        List<String> reasons = new ArrayList<>();

        boolean unsafe = forecasts.stream().anyMatch(f ->
                f.getRainProbability() > UNSAFE_RAIN_THRESHOLD ||
                        f.getWindKmh() > UNSAFE_WIND_THRESHOLD
        );

        if (unsafe) {
            reasons.add("Heavy rain or strong winds detected");
            return buildResponse(Classification.UNSAFE, reasons, forecasts);
        }

        boolean risky = forecasts.stream().anyMatch(f ->
                f.getRainProbability() > RISKY_RAIN_THRESHOLD
        );

        if (risky) {
            reasons.add("High chance of rain during event window");
            return buildResponse(Classification.RISKY, reasons, forecasts);
        }

        return buildResponse(
                Classification.SAFE,
                List.of("Weather conditions are stable"),
                forecasts
        );
    }

    private EventForecastResponse buildResponse(
            Classification classification,
            List<String> reasons,
            List<HourlyForecastResponse> forecasts) {

        return EventForecastResponse.builder()
                .classification(classification)
                .summary(reasons.get(0))
                .reason(reasons)
                .eventWindowForecast(forecasts)
                .build();
    }
}

