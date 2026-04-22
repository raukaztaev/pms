package org.example.pms.payments;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID orderId, UUID userId, BigDecimal amount, String status, Instant createdAt) {}
