package org.ultimateam.apiultimate.DTO.Weather_location;

import java.util.List;

public record Hourly(
        List<String> time,
        List<Double> temperature_2m,
        List<Integer> weather_code,
        List<Double> wind_speed_10m
) {}