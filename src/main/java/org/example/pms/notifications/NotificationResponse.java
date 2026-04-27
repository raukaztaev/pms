package org.example.pms.notifications;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, String type, String payload, String channel, Instant deliveredAt, Instant createdAt) {}
