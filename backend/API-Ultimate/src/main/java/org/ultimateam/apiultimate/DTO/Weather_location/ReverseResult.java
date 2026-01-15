package org.ultimateam.apiultimate.DTO.Weather_location;

import java.util.Map;

public record ReverseResult(Map<String, String> address) {
    public String getCityName() {
        if (address == null) return null;
        return address.getOrDefault("city",
                address.getOrDefault("town",
                        address.getOrDefault("village", "Inconnu")));
    }
}