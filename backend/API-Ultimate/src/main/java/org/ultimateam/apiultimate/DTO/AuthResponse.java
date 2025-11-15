package org.ultimateam.apiultimate.DTO;

public record AuthResponse(
        String token,
        String type
) {}