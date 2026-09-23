package com.stock.adapter.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, unique = true)
    private String sku;
    @Column(nullable = false)
    private String name;
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "alert_threshold", nullable = false)
    private int alertThreshold;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProductJpaEntity() { }

    public ProductJpaEntity(UUID id, String sku, String name, UUID categoryId, BigDecimal price, int quantity,
            int alertThreshold, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
        this.alertThreshold = alertThreshold;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public UUID getCategoryId() { return categoryId; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getAlertThreshold() { return alertThreshold; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
