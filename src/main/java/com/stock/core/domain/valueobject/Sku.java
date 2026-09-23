package com.stock.core.domain.valueobject;

public record Sku(String value) {
    public Sku {
        if (value == null || value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("SKU must be 3-50 chars");
        }
    }
}
