package com.stock.application.usecase;

import com.stock.application.dto.response.StockMovementDto;
import com.stock.core.domain.port.StockMovementRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListStockMovementsUseCase {
    private final StockMovementRepositoryPort repository;

    public ListStockMovementsUseCase(StockMovementRepositoryPort repository) { this.repository = repository; }

    public List<StockMovementDto> execute(UUID productId) {
        return repository.findByProductId(productId).stream()
                .map(movement -> new StockMovementDto(movement.id(), movement.productId(), movement.type(),
                    movement.quantity().value(), movement.reason(), movement.authorUsername(),
                    movement.movementDate()))
                .toList();
    }
}
