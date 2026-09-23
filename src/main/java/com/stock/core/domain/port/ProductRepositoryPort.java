package com.stock.core.domain.port;

import com.stock.core.domain.entity.Product;
import com.stock.core.domain.valueobject.Sku;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    List<Product> findAll();
    Optional<Product> findById(UUID id);
    Optional<Product> findBySku(Sku sku);
    Product save(Product product);
    void delete(UUID id);
    List<Product> findByAlertThresholdExceeded();
}
