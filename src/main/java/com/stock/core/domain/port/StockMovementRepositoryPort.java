package com.stock.core.domain.port;

import com.stock.core.domain.entity.StockMovement;
import java.util.List;
import java.util.UUID;

public interface StockMovementRepositoryPort {
    StockMovement save(StockMovement movement);
    List<StockMovement> findByProductId(UUID productId);
}
