package com.stock.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductDto(UUID id, String sku, String name, UUID categoryId, BigDecimal price,
        Integer quantity, Integer alertThreshold, Instant createdAt, Instant updatedAt) {
}
