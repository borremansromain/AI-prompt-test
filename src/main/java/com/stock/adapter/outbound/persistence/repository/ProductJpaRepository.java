package com.stock.adapter.outbound.persistence.repository;

import com.stock.adapter.outbound.persistence.entity.ProductJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {
    Optional<ProductJpaEntity> findBySku(String sku);
}
