package org.example.pms.users;

import java.time.Instant;
import java.util.UUID;

public record UserMeResponse(UUID id, String email, String role, Instant createdAt) {}
