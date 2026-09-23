package com.stock.application.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductCommand(
        @NotBlank @Size(min = 3, max = 50) String sku,
        @NotBlank @Size(max = 255) String name,
        @NotNull UUID categoryId,
        @NotNull @Positive BigDecimal price,
        @NotNull @PositiveOrZero Integer quantity,
        @NotNull @PositiveOrZero Integer alertThreshold) {
}
