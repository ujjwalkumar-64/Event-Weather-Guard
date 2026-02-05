package com.apora.eventweatherguard.service;

import com.apora.eventweatherguard.request.LocationRequest;
import com.apora.eventweatherguard.response.HourlyForecastResponse;
import com.apora.eventweatherguard.response.OpenMeteoResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class WeatherApiClient {

    private final WebClient webClient;


    public List<HourlyForecastResponse> getHourlyForecast(
            LocationRequest location,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        String url = buildUrl(location);

        OpenMeteoResponse response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(OpenMeteoResponse.class)
                .block();

        if (response == null || response.getHourly() == null) {
            throw new RuntimeException("No weather data available");
        }

        return mapAndFilter(
                response.getHourly(),
                startTime,
                endTime
        );
    }

    private String buildUrl(LocationRequest location) {
        return UriComponentsBuilder
                .fromUriString("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", location.getLatitude())
                .queryParam("longitude", location.getLongitude())
                .queryParam(
                        "hourly",
                        "precipitation_probability,wind_speed_10m"
                )
                .queryParam("timezone", "UTC")
                .toUriString();
    }

    private List<HourlyForecastResponse> mapAndFilter(
            OpenMeteoResponse.Hourly hourly,
            LocalDateTime start,
            LocalDateTime end) {

        List<HourlyForecastResponse> result = new ArrayList<>();

        for (int i = 0; i < hourly.getTime().size(); i++) {

            LocalDateTime forecastTime =
                    LocalDateTime.parse(hourly.getTime().get(i));

            if (!forecastTime.isBefore(start) &&
                    !forecastTime.isAfter(end)) {

                result.add(new HourlyForecastResponse(
                        forecastTime,
                        hourly.getPrecipitation_probability().get(i),
                        hourly.getWind_speed_10m().get(i)
                ));
            }
        }
        return result;
    }
}
