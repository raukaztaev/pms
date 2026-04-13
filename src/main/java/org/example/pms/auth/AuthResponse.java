package org.example.pms.auth;

import java.util.UUID;

public record AuthResponse(String accessToken, UserDto user) {
    public record UserDto(UUID id, String email, String role) {}
}
