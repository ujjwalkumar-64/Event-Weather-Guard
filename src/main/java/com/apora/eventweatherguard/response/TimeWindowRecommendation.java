package com.apora.eventweatherguard.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TimeWindowRecommendation {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int severityScore;
    private String reason;
}

