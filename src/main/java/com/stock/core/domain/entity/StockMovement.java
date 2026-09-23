package com.stock.core.domain.entity;

import com.stock.core.domain.valueobject.Quantity;
import java.time.Instant;
import java.util.UUID;

public record StockMovement(UUID id, UUID productId, StockMovementType type, Quantity quantity,
        String reason, String authorUsername, Instant movementDate, Instant createdAt) {
    public StockMovement {
        if (productId == null || type == null || quantity == null || quantity.value() == 0
                || authorUsername == null || authorUsername.isBlank()) {
            throw new IllegalArgumentException("Invalid stock movement");
        }
    }
}
