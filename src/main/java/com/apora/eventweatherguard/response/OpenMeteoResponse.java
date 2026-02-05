package com.apora.eventweatherguard.response;

import lombok.Data;

import java.util.List;

@Data
public class OpenMeteoResponse {

    private Hourly hourly;

    @Data
    public static class Hourly {
        private List<String> time;
        private List<Integer> precipitation_probability;
        private List<Double> wind_speed_10m;
    }
}
