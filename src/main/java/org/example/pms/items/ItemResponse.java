package org.example.pms.items;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ItemResponse(UUID id, UUID orderId, UUID userId, String name, String link, Integer quantity,
                           BigDecimal unitPrice, String notes, boolean distributed, Instant createdAt) {}
