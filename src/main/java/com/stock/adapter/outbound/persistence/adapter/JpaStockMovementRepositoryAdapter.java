package com.stock.adapter.outbound.persistence.adapter;

import com.stock.adapter.outbound.persistence.entity.StockMovementJpaEntity;
import com.stock.adapter.outbound.persistence.repository.StockMovementJpaRepository;
import com.stock.core.domain.entity.StockMovement;
import com.stock.core.domain.port.StockMovementRepositoryPort;
import com.stock.core.domain.valueobject.Quantity;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaStockMovementRepositoryAdapter implements StockMovementRepositoryPort {
    private final StockMovementJpaRepository repository;

    public JpaStockMovementRepositoryAdapter(StockMovementJpaRepository repository) { this.repository = repository; }

    public StockMovement save(StockMovement movement) {
        StockMovementJpaEntity entity = new StockMovementJpaEntity(movement.id(), movement.productId(), movement.type(),
                movement.quantity().value(), movement.reason(), movement.authorUsername(), movement.movementDate(),
                movement.createdAt());
        return toDomain(repository.save(entity));
    }

    public List<StockMovement> findByProductId(UUID productId) {
        return repository.findByProductIdOrderByMovementDateDesc(productId).stream().map(this::toDomain).toList();
    }

    private StockMovement toDomain(StockMovementJpaEntity entity) {
        return new StockMovement(entity.getId(), entity.getProductId(), entity.getType(),
                new Quantity(entity.getQuantity()), entity.getReason(), entity.getAuthorUsername(),
                entity.getMovementDate(), entity.getCreatedAt());
    }
}
