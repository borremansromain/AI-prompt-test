package com.stock.core.domain.exception;

import com.stock.core.domain.valueobject.Quantity;
import com.stock.core.domain.valueobject.Sku;

public class InsufficientStockException extends DomainException {
    public InsufficientStockException(Sku sku, Quantity requested) {
        super("Insufficient stock for " + sku.value() + ", requested: " + requested.value());
    }
}
