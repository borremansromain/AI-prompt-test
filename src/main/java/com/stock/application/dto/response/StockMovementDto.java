package com.stock.application.dto.response;

import com.stock.core.domain.entity.StockMovementType;
import java.time.Instant;
import java.util.UUID;

public record StockMovementDto(UUID id, UUID productId, StockMovementType type, Integer quantity,
        String reason, String authorUsername, Instant movementDate) {
}
