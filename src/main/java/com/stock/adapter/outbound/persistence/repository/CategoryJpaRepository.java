package com.stock.adapter.outbound.persistence.repository;

import com.stock.adapter.outbound.persistence.entity.CategoryJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {
    Optional<CategoryJpaEntity> findByName(String name);
    boolean existsByName(String name);
}
