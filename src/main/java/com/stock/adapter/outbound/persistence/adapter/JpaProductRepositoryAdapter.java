package com.stock.adapter.outbound.persistence.adapter;

import com.stock.adapter.outbound.persistence.entity.ProductJpaEntity;
import com.stock.adapter.outbound.persistence.repository.ProductJpaRepository;
import com.stock.core.domain.entity.Product;
import com.stock.core.domain.port.ProductRepositoryPort;
import com.stock.core.domain.valueobject.Money;
import com.stock.core.domain.valueobject.Quantity;
import com.stock.core.domain.valueobject.Sku;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaProductRepositoryAdapter implements ProductRepositoryPort {
    private final ProductJpaRepository repository;

    public JpaProductRepositoryAdapter(ProductJpaRepository repository) { this.repository = repository; }
    public List<Product> findAll() { return repository.findAll().stream().map(this::toDomain).toList(); }
    public Optional<Product> findById(UUID id) { return repository.findById(id).map(this::toDomain); }
    public Optional<Product> findBySku(Sku sku) { return repository.findBySku(sku.value()).map(this::toDomain); }
    public Product save(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity(product.id(), product.sku().value(), product.name(),
                product.categoryId(), product.price().amount(), product.quantity().value(), product.alertThreshold(),
                product.createdAt(), product.updatedAt());
        return toDomain(repository.save(entity));
    }
    public void delete(UUID id) { repository.deleteById(id); }
    public List<Product> findByAlertThresholdExceeded() {
        return findAll().stream()
            .filter(p -> p.alertThreshold() > 0 && p.quantity().value() <= p.alertThreshold())
            .toList();
    }

    private Product toDomain(ProductJpaEntity entity) {
        return new Product(entity.getId(), new Sku(entity.getSku()), entity.getName(), entity.getCategoryId(),
                new Money(entity.getPrice(), "EUR"), new Quantity(entity.getQuantity()), entity.getAlertThreshold(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
