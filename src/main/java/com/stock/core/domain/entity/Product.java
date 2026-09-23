package com.stock.core.domain.entity;

import com.stock.core.domain.exception.InsufficientStockException;
import com.stock.core.domain.valueobject.Money;
import com.stock.core.domain.valueobject.Quantity;
import com.stock.core.domain.valueobject.Sku;
import java.time.Instant;
import java.util.UUID;

public class Product {
    private final UUID id;
    private final Sku sku;
    private final String name;
    private final UUID categoryId;
    private final Money price;
    private Quantity quantity;
    private final int alertThreshold;
    private final Instant createdAt;
    private Instant updatedAt;

    public Product(UUID id, Sku sku, String name, UUID categoryId, Money price,
            Quantity quantity, int alertThreshold, Instant createdAt, Instant updatedAt) {
        if (sku == null || name == null || name.isBlank() || categoryId == null || price == null
                || quantity == null || alertThreshold < 0) {
            throw new IllegalArgumentException("Invalid product");
        }
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
        this.alertThreshold = alertThreshold;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;
    }

    public void applyStockMovement(StockMovementType type, Quantity delta) {
        if (type == StockMovementType.EXIT && delta.value() > quantity.value()) {
            throw new InsufficientStockException(sku, delta);
        }
        int next = type == StockMovementType.ENTRY
                ? quantity.value() + delta.value() : quantity.value() - delta.value();
        quantity = new Quantity(next);
        updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public Sku sku() { return sku; }
    public String name() { return name; }
    public UUID categoryId() { return categoryId; }
    public Money price() { return price; }
    public Quantity quantity() { return quantity; }
    public int alertThreshold() { return alertThreshold; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
