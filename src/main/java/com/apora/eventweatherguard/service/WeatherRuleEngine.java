package com.apora.eventweatherguard.service;

import com.apora.eventweatherguard.response.Classification;
import com.apora.eventweatherguard.response.EventForecastResponse;
import com.apora.eventweatherguard.response.HourlyForecastResponse;
import com.apora.eventweatherguard.response.TimeWindowRecommendation;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        int severity = calculateSeverity(forecasts);

        return EventForecastResponse.builder()
                .classification(classification)
                .severityScore(severity)
                .summary(reasons.get(0))
                .reason(reasons)
                .eventWindowForecast(forecasts)
                .build();

    }

    private int calculateSeverity(List<HourlyForecastResponse> forecasts) {

        return forecasts.stream()
                .mapToInt(f -> {
                    int rain = f.getRainProbability();
                    int wind = (int) Math.min((f.getWindKmh() / 50.0) * 100, 100);

                    return (int) (0.6 * rain + 0.4 * wind);
                })
                .max()
                .orElse(0);
    }

    public Optional<TimeWindowRecommendation> recommendTimeWindow(
            List<HourlyForecastResponse> forecasts,
            Duration eventDuration) {

        if (forecasts == null || forecasts.isEmpty()) {
            return Optional.empty();
        }

        int windowSize = (int) eventDuration.toHours();

        if (windowSize <= 0 || windowSize > forecasts.size()) {
            return Optional.empty();
        }

        int bestSeverity = Integer.MAX_VALUE;
        TimeWindowRecommendation bestWindow = null;

        for (int i = 0; i + windowSize <= forecasts.size(); i++) {

            List<HourlyForecastResponse> window =
                    forecasts.subList(i, i + windowSize);

            int severity = calculateSeverity(window);

            if (severity < bestSeverity) {

                bestSeverity = severity;

                LocalDateTime startTime =
                        LocalDateTime.now().plusHours(i);

                LocalDateTime endTime =
                        startTime.plus(eventDuration);

                bestWindow = new TimeWindowRecommendation(
                        startTime,
                        endTime,
                        severity,
                        "Lower rain probability and calmer winds"
                );
            }
        }

        return Optional.ofNullable(bestWindow);
    }


}

