package com.stock.adapter.outbound.persistence.repository;

import com.stock.adapter.outbound.persistence.entity.StockMovementJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementJpaEntity, UUID> {
    List<StockMovementJpaEntity> findByProductIdOrderByMovementDateDesc(UUID productId);
}
