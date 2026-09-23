package com.stock.application.dto.command;

import com.stock.core.domain.entity.StockMovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RecordStockMovementCommand(
        @NotNull UUID productId,
        @NotNull StockMovementType type,
        @NotNull @Positive Integer quantity,
        @Size(max = 1000) String reason) {
}
