package com.stock.core.domain.exception;

public class DuplicateSkuException extends DomainException {
    public DuplicateSkuException(String sku) {
        super("SKU already exists: " + sku);
    }
}
