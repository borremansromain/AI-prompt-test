package com.stock.adapter.outbound.persistence.entity;

import com.stock.core.domain.entity.StockMovementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
public class StockMovementJpaEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockMovementType type;
    @Column(nullable = false)
    private int quantity;
    private String reason;
    @Column(name = "author_username", nullable = false)
    private String authorUsername;
    @Column(name = "movement_date", nullable = false)
    private Instant movementDate;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected StockMovementJpaEntity() { }

    public StockMovementJpaEntity(UUID id, UUID productId, StockMovementType type, int quantity, String reason,
            String authorUsername, Instant movementDate, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.authorUsername = authorUsername;
        this.movementDate = movementDate;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public StockMovementType getType() { return type; }
    public int getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public String getAuthorUsername() { return authorUsername; }
    public Instant getMovementDate() { return movementDate; }
    public Instant getCreatedAt() { return createdAt; }
}
