package org.example.pms.orders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID id, UUID organizerId, String productName, String sourcePlatform,
                            BigDecimal minTargetAmount, BigDecimal currentTotal, String status,
                            Instant deadline, Instant createdAt) {}
