package org.example.pms.orders;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateOrderRequest(
        @NotBlank String productName,
        @NotBlank String sourcePlatform,
        @DecimalMin("0.00") BigDecimal minTargetAmount,
        @Future Instant deadline
) {}
