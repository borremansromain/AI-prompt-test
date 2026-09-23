package com.stock.core.domain.valueobject;

public record Quantity(int value) {
    public Quantity {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity must be >= 0");
        }
    }
}
