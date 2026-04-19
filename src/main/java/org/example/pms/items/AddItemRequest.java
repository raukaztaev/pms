package org.example.pms.items;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record AddItemRequest(
        @NotBlank String name,
        String link,
        @Min(1) Integer quantity,
        @DecimalMin("0.00") BigDecimal unitPrice,
        String notes
) {}
