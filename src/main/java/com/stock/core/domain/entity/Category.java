package com.stock.core.domain.entity;

import java.time.Instant;
import java.util.UUID;

public class Category {
    private final UUID id;
    private final String name;
    private final String description;
    private final Instant createdAt;

    public Category(UUID id, String name, String description, Instant createdAt) {
        if (name == null || name.isBlank() || name.length() > 255) {
            throw new IllegalArgumentException("Category name is required");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public static Category create(String name, String description, Instant createdAt) {
        return new Category(null, name, description, createdAt);
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public Instant createdAt() { return createdAt; }
}
